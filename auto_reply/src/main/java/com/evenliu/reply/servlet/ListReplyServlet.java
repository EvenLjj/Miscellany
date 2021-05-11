package com.evenliu.reply.servlet;

import com.evenliu.reply.entity.Page;
import com.evenliu.reply.service.QueryService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;


/**
 * Created by liu on 2017/1/5.
 */
public class ListReplyServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置编码
        req.setCharacterEncoding("UTF-8");
        //获取页面传过来的值
        String command=req.getParameter("command");
        String desctription=req.getParameter("description");
        String currentPage=req.getParameter("currentPage");
        //创建分页对象
        Page page=new Page();
        //验证当前页是否符合规范
        Pattern pattern=Pattern.compile("[0-9]{1,9}");
        if(currentPage==null||!pattern.matcher(currentPage).matches()){
            page.setCurrentPage(1);
        }else{
            page.setCurrentPage(Integer.valueOf(currentPage));
        }
        //获取数据并封装
        QueryService listService=new QueryService();
        req.setAttribute("messageList",listService.queryCommandAndContentList(command,desctription,page));
        req.setAttribute("page",page);
        req.getRequestDispatcher("/WEB-INF/jsp/back/list.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
