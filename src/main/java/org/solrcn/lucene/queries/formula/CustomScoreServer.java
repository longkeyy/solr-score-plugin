package org.solrcn.lucene.queries.formula;

import org.solrcn.search.compiler.DynamicQueryClassBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.apache.lucene.search.Query;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomScoreServer extends AbstractScoreServer {

    private final static Map<String, CuratorFramework> CuratorFrameworkInstances = Maps.newConcurrentMap();

    private final static Logger log = LoggerFactory.getLogger(CustomScoreServer.class);

    /**
     * zookeeper名称空间
     */
    public final static String DEFAULT_NAMESPACE = "search/site";

    /**
     * 服务名称
     */
    private final static String SERVICE_NAME = "scoreserver";

    /**
     * 站点名
     */
    private final String siteName;

    /**
     * zookeeper连接超时时间
     */
    public final static int DEFAULT_CONNECT_TIMEOUT = 10000;
    /**
     * zookeeper会话超时时间
     */
    public final static int DEFAULT_SESSION_TIMEOUT = 30000;

    /**
     * zookeeper对象
     */
    private CuratorFramework zkClient;

    private boolean connected;

    /**
     * 发现公式服务
     */
    private ServiceDiscovery<String> serviceDiscovery;

    /**
     * 发现公式变更
     */
    private ServiceCache<String> serviceCache;

    /**
     * @param zkhost   zk连接地址
     * @param siteName 站点名
     * @throws Exception
     */
    public CustomScoreServer(String zkhost, String siteName) throws Exception {
        this(zkhost, siteName, null, null);
    }

    /**
     * @param zkhost                  zk连接地址
     * @param site                    站点名
     * @param luceneQueryClassBuilder 初始化实例
     * @param originalFlumlas
     * @throws Exception
     */
    public CustomScoreServer(String zkhost,
                             String site,
                             DynamicQueryClassBuilder luceneQueryClassBuilder,
                             Map<String, String> originalFlumlas)
            throws Exception {
        super(luceneQueryClassBuilder, originalFlumlas);
        this.siteName = site;
        if (!initoriginalFlumlas(originalFlumlas, null)) {
            log.info("CustomScoreServer just init config from zk");
        }
        this.zkClient = newZkClient(zkhost, DEFAULT_NAMESPACE, DEFAULT_CONNECT_TIMEOUT, DEFAULT_SESSION_TIMEOUT);
    }

    /**
     * 初始化公式发现服务
     *
     * @return
     */
    public boolean initServiceDiscovery() {
        serviceDiscovery = ServiceDiscoveryBuilder.builder(String.class).client(zkClient).basePath(siteName).build();
        try {
            serviceDiscovery.start();
            return true;
        } catch (Exception e) {
            log.warn("initServiceDiscovery error", e);
            return false;
        }
    }

    /**
     * 初始化公式变更监听服务
     *
     * @return
     */
    public boolean initServiceCache() {
        serviceCache = serviceDiscovery.serviceCacheBuilder().name(SERVICE_NAME).build();
        serviceCache.addListener(new ServiceCacheListener() {

            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                switch (connectionState) {
                    case RECONNECTED:
                        refreshFlumla();
                        break;
                    case CONNECTED:
                    case LOST:
                    case READ_ONLY:
                    case SUSPENDED:
                    default:
                        break;
                }
            }

            @Override
            public void cacheChanged() {
                refreshFlumla();
            }
        });
        try {
            serviceCache.start();
            return true;
        } catch (Exception e) {
            log.warn("initServiceCache error", e);
            return false;
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see com.dhgate.lucene.queries.formula.ScoreServer#refreshFlumla()
     */
    @Override
    public boolean refreshFlumla() {
        String newLuceneQueryClassTemplate = refreshClassTemplate();
        if (Strings.isNullOrEmpty(newLuceneQueryClassTemplate))
            return false;
        if (serviceCache == null)
            return false;

        List<ServiceInstance<String>> newInstances = serviceCache.getInstances();
        if (newInstances != null) {
            Map<String, String> newOriginalFlumlas = Maps.newHashMap();
            log.info("found newsize: {} ", newInstances.size());
            for (ServiceInstance<String> serviceInstance : newInstances) {
                newOriginalFlumlas.put(serviceInstance.getId(), serviceInstance.getAddress());
            }
            initoriginalFlumlas(newOriginalFlumlas, newLuceneQueryClassTemplate);
            return true;
        } else {
            log.info("serviceCache null , update faild");
            return false;
        }
    }

    public String getSiteName() {
        return siteName;
    }

    @Override
    public Constructor getFlumlaById(String id) {
        return formulaConstructors.get(id);
    }

    @Override
    public Set<String> getFlumlaIds() {
        return formulaConstructors.keySet();
    }

    @Override
    public Map<String, Constructor<Query>> getFlumlas() {
        return formulaConstructors;
    }

    @Override
    public boolean addFlumla(String id, CustomFlumla flumla) throws Exception {
        if (!isConnected()) {
            log.info("zk connect not ready");
            return false;
        }
        ServiceInstance instance = ServiceInstance.builder().name(SERVICE_NAME).serviceType(ServiceType.STATIC).id(id)
                .address(flumla.getFlumlaStr()).build();
        serviceDiscovery.registerService(instance);
        return true;
    }

    @Override
    public boolean removeFlumla(String id) throws Exception {
        if (!isConnected()) {
            log.info("zk connect not ready");
            return false;
        }
        ServiceInstance<String> instance = ServiceInstance.<String>builder().name(SERVICE_NAME).id(id).build();
        serviceDiscovery.unregisterService(instance);
        return true;
    }

    @Override
    public void disconnect() {
        Closeables.closeQuietly(serviceCache);
        Closeables.closeQuietly(serviceDiscovery);
        Closeables.closeQuietly(zkClient);
    }

    /**
     * @param connectString       zk连接串
     * @param namespace           名称空间
     * @param connectionTimeoutMs 连接超时时间
     * @param sessionTimeoutMs    会话超时时间
     * @return
     */
    private CuratorFramework newZkClient(String connectString, String namespace, int connectionTimeoutMs,
                                         int sessionTimeoutMs) {
        String CuratorFrameworkInstancesKey = connectString + "/" + namespace;
        CuratorFramework curatorFramework = CuratorFrameworkInstances.get(CuratorFrameworkInstancesKey);
        if (curatorFramework != null) {
            return curatorFramework;
        }
        {
            synchronized (CuratorFrameworkInstances) {
                curatorFramework = CuratorFrameworkInstances.get(CuratorFrameworkInstancesKey);
                if (curatorFramework != null) {
                    return curatorFramework;
                }

                Builder builder = CuratorFrameworkFactory.builder().connectString(connectString)
                        .connectionTimeoutMs(connectionTimeoutMs).sessionTimeoutMs(sessionTimeoutMs)
                        .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 5000));

                if (!Strings.isNullOrEmpty(namespace))
                    builder.namespace(namespace);

                CuratorFramework client = builder.build();

                client.getConnectionStateListenable().addListener(new ConnectionStateListener() {

                    @Override
                    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

                        switch (connectionState) {
                            case RECONNECTED:
                                setConnected(true);
                                break;
                            case CONNECTED:
                                if (!Strings.isNullOrEmpty(DEFAULT_NAMESPACE))
                                    curatorFramework.newNamespaceAwareEnsurePath(DEFAULT_NAMESPACE);
                                while (!initServiceDiscovery())
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        continue;
                                    }
                                while (!initServiceCache()) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        continue;
                                    }
                                }
                                refreshFlumla();
                                setConnected(true);
                                break;
                            case LOST:
                                setConnected(false);
                                break;
                            case READ_ONLY:
                            case SUSPENDED:
                            default:
                                break;
                        }

                    }
                });
                client.start();
                CuratorFrameworkInstances.put(namespace, client);
                return client;
            }
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * 从zk获取类模板
     *
     * @return
     */
    private String refreshClassTemplate() {
        try {
            Stat stat = zkClient.checkExists().forPath(siteName + "/" + SERVICE_NAME);
            if (stat != null) {
                byte[] forPath = zkClient.getData().forPath(siteName + "/" + SERVICE_NAME);
                return new String(forPath);
            }
        } catch (Exception e) {
            log.warn("gettempclass faild...", e);
        }
        return "";
    }

    /**
     * 上传类模板
     *
     * @param classTemplate
     */
    public void uploadClassTemplate(byte[] classTemplate) {
        zkClient.newNamespaceAwareEnsurePath(siteName + "/" + SERVICE_NAME);
        try {
            zkClient.setData().forPath(siteName + "/" + SERVICE_NAME, classTemplate);
        } catch (Exception e) {
            log.warn("get tempclass faild, zk error...", e);
        }
    }

    @Override
    public Map<String, String> getOriginalFlumlas() {
        return originalFlumlas;
    }

}