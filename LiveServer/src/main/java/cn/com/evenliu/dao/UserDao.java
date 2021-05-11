package cn.com.evenliu.dao;

import cn.com.evenliu.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by liu on 2016/11/19.
 */
public interface UserDao {

    public int addUser(@Param("username") String username, @Param("nickname")String nickname,@Param("password")String password);

    public User getUserName(int id);

    public int updatePassWord(@Param("id")int id,@Param("password")String password);

    public int deleteUser(int id);

    public List<User> queryAll(@Param("offset") int offset,@Param("limit") int limit);
}
