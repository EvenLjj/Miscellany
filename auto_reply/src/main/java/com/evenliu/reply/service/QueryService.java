package com.evenliu.reply.service;

import com.evenliu.reply.bean.Command;
import com.evenliu.reply.bean.CommandContent;
import com.evenliu.reply.dao.CommandDao;
import com.evenliu.reply.entity.Page;
import com.evenliu.reply.util.Iconst;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by liu on 2017/1/6.
 * 列表相关的业务功能
 */
public class QueryService {

    public List<Command> queryCommandAndContentList(String name, String desctription,Page page){
        Command command=new Command();
        command.setName(name);
        command.setDescription(desctription);
        CommandDao commandDao=new CommandDao();
        //根据条件查询条数（每次请求分页都查询总数）
        int totalNumber=commandDao.count(command);
        page.setTotalNumber(totalNumber);
        //在这里设置不合理且出现错误
        //page.setPageNumber(2);
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("command",command);
        params.put("page",page);
        return commandDao.queryCommandAndContentList(params);
    }

    public String queryByCommand(String name){
        List<Command> commandList;
        CommandDao commandDao=new CommandDao();
        Command command=new Command();
        command.setName(name);
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("command",command);
        if(Iconst.HELP_COMMAND.equals(name)){
            commandList=commandDao.queryCommandAndContentList(null);
            StringBuilder result=new StringBuilder();
            for(int i=0;i<commandList.size();i++){
                if(i!=0){
                    result.append("<br/>");
                }
                result.append("回复【"+commandList.get(i).getName()+"]可以查看" + commandList.get(i).getDescription());
            }
            return result.toString();
        }
        commandList=commandDao.queryCommandAndContentList(params);
        if(commandList.size()>0){
            List<CommandContent> contentList=commandList.get(0).getContentList();
            int i=new Random().nextInt(contentList.size());
            return contentList.get(i).getContent();
        }else{
            return Iconst.NO_MATCHING_CONTENT;
        }
    }
}
