package com.evenliu.reply.dao;

import com.evenliu.reply.bean.Command;
import com.evenliu.reply.db.DBAccess;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by liu on 2017/1/6.
 */
public class CommandDao {
    public List<Command> queryCommandAndContentList(Map<String,Object> params){
        List<Command> commandList=null;
        DBAccess dbAccess=new DBAccess();
        SqlSession sqlSession=null;
        try {
            sqlSession=dbAccess.getSqlSession();
            //通过sqlSession来获取SQL语句
            //commandList=sqlSession.selectList("queryCommandList",command);
            //面向接口的方式
            IcommandDao icommandDao=sqlSession.getMapper(IcommandDao.class);
            commandList=icommandDao.queryCommandAndContentList(params);
        }catch (IOException e){
            e.printStackTrace();;
        }finally {
            if(sqlSession!=null) {
                sqlSession.close();
            }
        }
        return commandList;
    }
    public int count(Command command){
        int result=0;
        DBAccess dbAccess=new DBAccess();
        SqlSession sqlSession=null;
        try {
            sqlSession=dbAccess.getSqlSession();
            //通过sqlSession来获取SQL语句
            //commandList=sqlSession.selectList("queryCommandList",command);
            //面向接口的方式
            IcommandDao icommandDao=sqlSession.getMapper(IcommandDao.class);
            result=icommandDao.count(command);
        }catch (IOException e){
            e.printStackTrace();;
        }finally {
            if(sqlSession!=null) {
                sqlSession.close();
            }
        }
        return result;
    }
}
