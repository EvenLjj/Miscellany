package com.evenliu.reply.service;

import com.evenliu.reply.dao.MessageDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 2017/1/6.
 */
public class MaintainService {

    public void deleteMessageById(String id){
        int messageId=Integer.valueOf(id);
        MessageDao messageDao=new MessageDao();
        messageDao.deleteMessageById(messageId);
    }

    public void deleteMessages(String[] ids){
        MessageDao messageDao=new MessageDao();
        List<Integer> idList=new ArrayList<Integer>();
        for(String id:ids){
            idList.add(Integer.valueOf(id));
        }
        messageDao.deleteMessages(idList);
    }

}
