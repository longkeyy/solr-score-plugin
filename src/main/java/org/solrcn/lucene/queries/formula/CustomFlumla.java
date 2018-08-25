package org.solrcn.lucene.queries.formula;

import org.codehaus.jackson.annotate.JsonTypeName;


/**
 * @author Administrator
 *
 *排序公式封装
 */
@JsonTypeName(value = "details")
public class CustomFlumla {
	
	private String id;
	private String flumlaStr;
	
	public CustomFlumla() {
	}
	
	/**
	 * @param id		公式名
	 * @param flumlaStr 公式
	 */
	public CustomFlumla(String id, String flumlaStr) {
		this.id = id;
		this.flumlaStr = flumlaStr.trim().replaceAll("[\\n\\r\\s]","");
	}
	
	/**
	 * @return	公式名
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * @return 公式
	 */
	public String getFlumlaStr(){
		return flumlaStr;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}

	public void setFlumlaStr(String flumlaStr) {
		this.flumlaStr = flumlaStr;
	}
	
	@Override
	public String toString() {
		return flumlaStr;
	}
}