package com.evenliu.reply.servlet;

import com.evenliu.reply.service.MaintainService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liu on 2017/1/6.
 */
public class DeleteMessageByIdServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置编码
        req.setCharacterEncoding("UTF-8");
        //获取页面传过来的值
        String id=req.getParameter("id");
        //获取数据并封装
        MaintainService maintainService=new MaintainService();
        maintainService.deleteMessageById(id);
        req.getRequestDispatcher("/list.action").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
