package org.solrcn.lucene.queries.formula;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.lucene.search.Query;
import org.apache.solr.request.SolrQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solrcn.search.compiler.DynamicQueryClassBuilder;
import com.google.common.collect.Maps;

public abstract class AbstractScoreServer implements ScoreServer {
	
	private final static Logger log = LoggerFactory.getLogger(AbstractScoreServer.class);
	
	protected DynamicQueryClassBuilder luceneQueryClassBuilder;
	/**
	 * 公式实例
	 */
	protected Map<String, Constructor<Query>> formulaConstructors = Maps.newHashMap();
	
	/**
	 * 公式内容
	 */
	protected Map<String, String> originalFlumlas =  Maps.newConcurrentMap();
	
	public AbstractScoreServer(DynamicQueryClassBuilder luceneQueryClassBuilder, Map<String, String> originalFlumlas) {
		if (luceneQueryClassBuilder != null)
			this.luceneQueryClassBuilder = luceneQueryClassBuilder;
		if (originalFlumlas != null)
			this.originalFlumlas = originalFlumlas;
	}

	/**
	 *
	 * @param dynamicQueryClassBuilder
	 * @param serviceInstance
	 * @return
	 */
	private Constructor<Query> getConstructor(final DynamicQueryClassBuilder dynamicQueryClassBuilder, final ServiceInstance<String> serviceInstance){
		return getConstructor(dynamicQueryClassBuilder, originalFlumlas.get(serviceInstance.getId()), serviceInstance.getAddress());
	}


	/**
	 *
	 * @param dynamicQueryClassBuilder
	 * @param id
	 * @param formulaContent
	 * @return
	 */
	protected Constructor<Query> getConstructor(final DynamicQueryClassBuilder dynamicQueryClassBuilder, final String id, final String formulaContent){
		if(formulaContent.equals(originalFlumlas.get(id)) && formulaConstructors.containsKey(id)){
			return formulaConstructors.get(id);
		} else {
			Constructor<Query> constructor = null;
			try {
				Class build = dynamicQueryClassBuilder.build(id, formulaContent);
				if(build==null){
					return null;
				}
				return build.getConstructor(Query.class, SolrQueryRequest.class);
//				Constructor<Query>[] constructors = build.getConstructors();
//				for (int i = 0; i < constructors.length; i++) {
//					if(constructors[i].getParameterTypes().length==2){
//						constructor = constructors[i];	
//					}
//				}				
//				return constructor;
			} catch (Exception e) {
				log.warn("flumla {} build faild...", id, e);
			}
		}
		log.warn("flumla {} got faild...", id);
		return null;
	}
	
	protected boolean initoriginalFlumlas(final Map<String, String> newOriginalFlumlas,
										  final String newLuceneQueryClassTemplate){
		//如果有新的模板就根据新的模板生成类工厂
		DynamicQueryClassBuilder newBuilder = newLuceneQueryClassTemplate == null ? luceneQueryClassBuilder
				: new DynamicQueryClassBuilder(newLuceneQueryClassTemplate);
		
		if(newBuilder == null) return false;
		
		Map<String, Constructor<Query>> newFormulaConstructors = Maps.newHashMap();
		Map<String, String> newformulaContents = Maps.newHashMap();
		
		for (String id : newOriginalFlumlas.keySet()) {
			String flumlasContent = newOriginalFlumlas.get(id);
			Constructor<Query> newConstructor = getConstructor(newBuilder, id, flumlasContent);
			if (newConstructor != null) {
				newFormulaConstructors.put(id, newConstructor);
				newformulaContents.put(id, flumlasContent);
			}
		}
		if (newFormulaConstructors.size() > 0) {
			this.formulaConstructors = newFormulaConstructors;
			this.originalFlumlas = newformulaContents;
			this.luceneQueryClassBuilder = newBuilder;
			log.info("update newsize: {}, update finish",newFormulaConstructors.size());
			return true;
		} else {
			log.info("update newsize: {}, update skip",newFormulaConstructors.size());
			return false;
		}
	}
}
