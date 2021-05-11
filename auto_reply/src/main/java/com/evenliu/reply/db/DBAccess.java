package com.evenliu.reply.db;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by liu on 2017/1/6.
 */
public class DBAccess {
    public SqlSession getSqlSession() throws IOException{
        //通过配置文件获取数据库连接信息
        Reader reader=Resources.getResourceAsReader("mabatis.xml");
        //通过配置信息构件一个sqlsessionfactoey
        SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession=sqlSessionFactory.openSession();
        return sqlSession;
    }
}
