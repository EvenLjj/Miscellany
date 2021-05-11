package com.evenliu.reply.dao;

import com.evenliu.reply.bean.Command;
import java.util.List;
import java.util.Map;

/**
 * Created by liu on 2017/1/7.
 */
public interface IcommandDao {

    public List<Command> queryCommandAndContentList(Map<String,Object> params);

    public int count(Command command);
}
