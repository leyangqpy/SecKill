package com.leyang.miaosha.dao;

import com.leyang.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by qianpyn on 2018/5/24.
 */
@Mapper
public interface UserDao {

    @Select("SELECT * FROM tb_user where id = #{id}")
    public User getUserById(@Param("id") int id);

    @Insert("insert into tb_user(id,name)values(#{id},#{name})")
    public int insert(User user);
}
