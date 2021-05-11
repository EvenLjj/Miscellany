package com.evenliu.lucene.test.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;


public class SolrJTest {

	public static void main(String[] args) throws Exception {
		//addDocument();
		//queryDocument();
		deleteDocument();
	}


	public static void addDocument() throws Exception {
		//创建一连接
		SolrClient solrServer = new HttpSolrClient.Builder("http://192.168.72.130:8983/solr/test").build();
		//创建一个文档对象
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "2");
		//把文档对象写入索引库
		solrServer.add(document);
		//提交
		solrServer.commit();
	}

	public static void deleteDocument() throws Exception {
		//创建一连接
		SolrClient solrServer = new HttpSolrClient.Builder("http://192.168.72.130:8983/solr/test").build();
		//solrServer.deleteById("test001");
		solrServer.deleteByQuery("*:*");
		solrServer.commit();
	}

	public static void queryDocument() throws Exception {
		SolrClient solrServer = new HttpSolrClient.Builder("http://192.168.72.130:8983/solr/test").build();
		//创建一个查询对象
		SolrQuery query = new SolrQuery();
		//设置查询条件
		query.setQuery("*:*");
		query.setStart(20);
		query.setRows(50);
		//执行查询
		QueryResponse response = solrServer.query(query);
		//取查询结果
		SolrDocumentList solrDocumentList = response.getResults();
		System.out.println("共查询到记录：" + solrDocumentList.getNumFound());
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
		}
	}
}
