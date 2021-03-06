package org.ye.psys.db.service;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ye.psys.db.entity.User;
import org.ye.psys.db.entity.UserExample;
import org.ye.psys.db.mapper.UserMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insertSelective(user);
    }

    public int updateById(User user){
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public int count() {
        UserExample example = new UserExample();
        example.or().andDeletedEqualTo(false);
        return (int) userMapper.countByExample(example);
    }

    public List<User> querySelective(String username, String mobile, Integer page, Integer size, String sort, String order) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andNicknameLike("%" + username + "%");
        }
        if (!StringUtils.isEmpty(mobile)) {
            criteria.andMobileEqualTo(mobile);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return userMapper.selectByExample(example);
    }

    public int findBySex(int gender) {
        UserExample example = new UserExample();
        example.or().andGenderEqualTo((byte) gender).andDeletedEqualTo(false);
        return (int) userMapper.countByExample(example);
    }


    public List<User> queryByMobile(String mobile) {
        UserExample example = new UserExample();
        example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }
}
