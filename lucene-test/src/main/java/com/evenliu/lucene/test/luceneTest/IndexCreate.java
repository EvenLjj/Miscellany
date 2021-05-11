package com.evenliu.lucene.test.luceneTest;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;


/**
 * Created by liujianjun02 on 2017/8/12.
 */
public class IndexCreate {
    /**
     * 创建索引
     * @param args
     */
    public static void main(String[] args){

        Analyzer analyzer= new StandardAnalyzer(Version.LUCENE_43);
        IndexWriterConfig indexWriterConfig=new IndexWriterConfig(Version.LUCENE_43,analyzer);
        //没有就新建，有就打开
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        Directory directory=null;
        IndexWriter indexWriter=null;
        try {
            String path="D://index/test";
            directory= FSDirectory.open(new File(path));
            if(indexWriter.isLocked(directory)){
                indexWriter.unlock(directory);
            }
            indexWriter=new IndexWriter(directory,indexWriterConfig);
            File file = new File("E:/lucene/technology.txt");
            Document doc1=new Document();
            //为文档添加域
            doc1.add(new StringField("id",file.getName(), Field.Store.YES));
            doc1.add(new TextField("content","hackweeek",Field.Store.YES));
            doc1.add(new IntField("num",1, Field.Store.YES));
            indexWriter.addDocument(doc1);


            Document doc2=new Document();
            //为文档添加域
            doc2.add(new StringField("id","hi", Field.Store.YES));
            doc2.add(new TextField("content","黑米周末",Field.Store.YES));
            doc2.add(new IntField("num",2, Field.Store.YES));
            indexWriter.addDocument(doc2);

            indexWriter.commit();
            indexWriter.close();
            directory.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //读取文件内容
    public static String GetFileContent(File file) throws Exception{

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuffer content = new StringBuffer();
        String line = reader.readLine();
        while(line!=null){
            content.append(line).append("\n");
        }
        return content.toString();

    }
}
