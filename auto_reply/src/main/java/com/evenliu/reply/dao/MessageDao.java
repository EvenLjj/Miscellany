package com.evenliu.reply.dao;

import com.evenliu.reply.bean.Message;
import com.evenliu.reply.db.DBAccess;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;

/**
 * Created by liu on 2017/1/6.
 */
public class MessageDao {

    public List<Message> queryAllMessage(String command,String desctription){
        List<Message> messageList=null;
        DBAccess dbAccess=new DBAccess();
        SqlSession sqlSession=null;
        try {
            sqlSession=dbAccess.getSqlSession();
            Message message=new Message();
            message.setCommand(command);
            message.setDescription(desctription);
            //通过sqlSession来获取SQL语句
            messageList=sqlSession.selectList("queryAllMessage",message);
        }catch (IOException e){
            e.printStackTrace();;
        }finally {
            if(sqlSession!=null) {
                sqlSession.close();
            }
        }
        return messageList;
    }

    /**
     * 删除单条记录
     * @param id
     */
    public void deleteMessageById(int id){
        DBAccess dbAccess=new DBAccess();
        SqlSession sqlSession=null;
        try {
            sqlSession=dbAccess.getSqlSession();
            //通过sqlSession来获取SQL语句
            sqlSession.delete("deleteMessageById",id);
            //增删改需要手动提交
            sqlSession.commit();
        }catch (IOException e){
            e.printStackTrace();;
        }finally {
            if(sqlSession!=null) {
                sqlSession.close();
            }
        }
    }

    public void deleteMessages(List<Integer> list){
        DBAccess dbAccess=new DBAccess();
        SqlSession sqlSession=null;
        try {
            sqlSession=dbAccess.getSqlSession();
            //通过sqlSession来获取SQL语句
            sqlSession.delete("deleteMessages",list);
            //增删改需要手动提交
            sqlSession.commit();
        }catch (IOException e){
            e.printStackTrace();;
        }finally {
            if(sqlSession!=null) {
                sqlSession.close();
            }
        }
    }
}
