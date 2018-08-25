package org.solrcn.lucene.queries.formula;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;

import org.apache.lucene.search.Query;

/**
 * 评分服务
 * @author chenyi
 *
 */
public interface ScoreServer {

	/**
	 * 根据公式编号获取公式
	 * @param id
	 * @return
	 */
	public Constructor<Query> getFlumlaById(String id);
	
	/**
	 * 获取所有公式编号
	 * @return
	 */
	public Collection<String> getFlumlaIds();
	
	/**
	 * 刷新公式
	 * @return
	 */
	public boolean refreshFlumla();

	/**
	 * 添加公式，如果公式已经存在则覆盖
	 * @param id	公式编号（唯一）
	 * @param flumla	公式内容
	 * @throws Exception
	 */
	public boolean addFlumla(String id, CustomFlumla flumla) throws Exception;
	
	/**
	 * 删除公式，以公式编号作为标识
	 * @param id
	 * @throws Exception
	 */
	public boolean removeFlumla(String id) throws Exception;
	
	/**
	 * 获取所有公式
	 * @return
	 */
	public Map<String, Constructor<Query>> getFlumlas();
	
	
	/**
	 * 获取原始公式
	 * @return
	 */
	public Map<String, String> getOriginalFlumlas();
	
	/**
	 * 断开连接
	 */
	void disconnect();
	

}
