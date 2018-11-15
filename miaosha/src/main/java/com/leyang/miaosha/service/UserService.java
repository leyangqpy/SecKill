package com.leyang.miaosha.service;

import com.leyang.miaosha.dao.UserDao;
import com.leyang.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by qianpyn on 2018/5/24.
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    public User getById(int id) {
        return userDao.getUserById(id);
    }

    @Transactional
    public boolean tx() {
        User u1 = new User();
        u1.setId(2);
        u1.setName("2222");
        userDao.insert(u1);
        User u2 = new User();
        u2.setId(1);
        u2.setName("3565");
        userDao.insert(u2);

        return true;
    }

}
