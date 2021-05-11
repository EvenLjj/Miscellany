package com.evenliu.reply.dao;

import com.evenliu.reply.bean.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 2017/1/6.
 */
public class MessageByJDBCDao {

    /**
     * 查询所有消息
     * @param command
     * @param desctription
     * @return
     */
    public List<Message> queryAllMessage(String command,String desctription){
        List<Message> messageList=new ArrayList<Message>();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/micro_message","root","root");
            StringBuilder sql=new StringBuilder("select ID,COMMAND,DESCRIPTION,CONTENT from message where 1=1");
            List<String> params=new ArrayList<String>();
            if(command!=null&&!"".equals(command.trim())){
                sql.append(" and COMMAND=?");
                params.add(command);
            }
            if(desctription!=null&&!"".equals(desctription.trim())){
                sql.append(" and DESCRIPTION like '%' ? '%'");
                params.add(desctription);
            }
            PreparedStatement statement=conn.prepareStatement(sql.toString());
            for(int i=0;i<params.size();i++){
                statement.setString(i+1,params.get(i));
            }
            ResultSet rs=statement.executeQuery();
            while(rs.next()){
                Message message=new Message();
                messageList.add(message);
                message.setId(rs.getString("ID"));
                message.setCommand(rs.getString("COMMAND"));
                message.setDescription(rs.getString("DESCRIPTION"));
                message.setContent(rs.getString("CONTENT"));
            }
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        } catch(SQLException e){
            e.printStackTrace();
        }
        return messageList;
    }
}
