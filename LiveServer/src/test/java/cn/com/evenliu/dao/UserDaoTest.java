package cn.com.evenliu.dao;

import cn.com.evenliu.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by liu on 2016/11/19.
 */

/*
*配置spring与junit的整合，junit启动时加载springIOC容器
* */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class UserDaoTest {

    @Resource
    private UserDao userDao;

    @Test
    public void addUser() throws Exception {
        int i=userDao.addUser("liujianjun","evenliu","123");
        System.out.println(i);
    }

    @Test
    public void getUserName() throws Exception {
        User user=userDao.getUserName(5);
        if(user!=null){
            System.out.println(user.getNickname());
        }

    }

    @Test
    public void updatePassWord() throws Exception {
        System.out.println(userDao.updatePassWord(2,"3210"));
    }

    @Test
    public void deleteUser() throws Exception {
        System.out.println(userDao.deleteUser(4));
    }

    @Test
    public void queryAll() throws Exception {
        List<User> users=userDao.queryAll(0,3);
        for(int i=0;i<users.size();i++){
            System.out.println(users.get(i).getNickname());
        }
    }

}