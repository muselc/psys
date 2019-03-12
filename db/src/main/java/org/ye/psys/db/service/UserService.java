package org.ye.psys.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.User;
import org.ye.psys.db.entity.UserExample;
import org.ye.psys.db.mapper.UserMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findById(Integer userId){
        return userMapper.selectByPrimaryKey(userId);
    }

    public User findByWxId(String openId){
        UserExample example = new UserExample();
        example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    public void add(User user){
        user.setAddTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insertSelective(user);
    }

    public int updateById(User user){
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateByPrimaryKeySelective(user);
    }
}
