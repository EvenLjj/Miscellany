 /**  
 *@Description:     
 */ 
package com.evenliu.lucene.test.test;

import java.util.HashSet;

import com.evenliu.lucene.test.lucene.index.model.ConfigBean;
import com.evenliu.lucene.test.lucene.index.model.IndexConfig;
import com.evenliu.lucene.test.lucene.index.model.SearchResultBean;
import com.evenliu.lucene.test.lucene.index.operation.NRTIndex;
import com.evenliu.lucene.test.lucene.index.operation.NRTSearch;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;


  
public class IndexManagerTest {

	public static void main(String[] args) throws ParseException {
		HashSet<ConfigBean> set = new HashSet<ConfigBean>();
		for (int i = 0; i < 4; i++) {
			ConfigBean bean = new ConfigBean();
			bean.setIndexPath("d:/jkxy");
			bean.setIndexName("test" + i);
			set.add(bean);
		}
		IndexConfig.setConfig(set);
		String indexName = "test0";
		NRTIndex index = new NRTIndex(indexName);
		Document doc1 = new Document();
		doc1.add(new StringField("id", "1", Store.YES));
		doc1.add(new TextField("content", "极客学院", Store.YES));
		index.addDocument(doc1);
		doc1 = new Document();
		doc1.add(new StringField("id", "2", Store.YES));
		doc1.add(new TextField("content", "Lucene案例开发", Store.YES));
		index.addDocument(doc1);
		index.commit();
		System.out.println("已经向索引添加两条记录");
		NRTSearch search = new NRTSearch(indexName);
		QueryParser parse = new QueryParser(Version.LUCENE_43, "content", new StandardAnalyzer(Version.LUCENE_43));
		Query query = parse.parse("极客学院Lucene案例开发课程");
		SearchResultBean bean = search.search(query, 0, 10);
		System.out.println("第一次查询" + bean.getCount());
		doc1 = new Document();
		doc1.add(new StringField("id", "2", Store.YES));
		doc1.add(new TextField("content", "", Store.YES));
		Term term = new Term("id", "2");
		index.updateDocument(term, doc1);
		index.commit();
		System.out.println("第一次修改一条记录");
		bean = search.search(query, 0, 10);
		System.out.println("第二次查询" + bean.getCount());
	}

}
