package com.evenliu.lucene.test.luceneTest;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;

/**
 * Created by liujianjun02 on 2017/8/12.
 */
public class IndexSearch {
    /**
     * 索引的搜索
     *
     * @param args
     */
    public static void main(String[] args){
        Directory directory=null;
        try{
            directory= FSDirectory.open(new File("D://index/test"));
            DirectoryReader directoryReader=DirectoryReader.open(directory);
            IndexSearcher searcher=new IndexSearcher(directoryReader);
            Analyzer analyzer=new StandardAnalyzer(Version.LUCENE_43);
            QueryParser parser=new QueryParser(Version.LUCENE_43,"id",analyzer);
            Query query=parser.parse("hi");
            TopDocs topDocs=searcher.search(query,10);
            if(topDocs!=null){
                System.out.println("符合条件的文档总数："+topDocs.totalHits);
                for(int i=0;i<topDocs.scoreDocs.length;i++){
                    Document doc=searcher.doc(topDocs.scoreDocs[i].doc);
                    System.out.println("id="+doc.get("id"));
                    System.out.println("content="+doc.get("content"));
                    System.out.println("num="+doc.get("num"));
                }
            }

            directory.close();
            directoryReader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
