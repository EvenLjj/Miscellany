 /**  
 *@Description:     
 */ 
package com.evenliu.lucene.test.lucene.index.model;

import java.util.List;

import org.apache.lucene.document.Document;

public class SearchResultBean {
	private int count;//符合条件的总记录条数
	private List<Document> docs;//查询的结果集
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<Document> getDocs() {
		return docs;
	}
	public void setDocs(List<Document> docs) {
		this.docs = docs;
	}
}
