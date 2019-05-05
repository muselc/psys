package org.ye.psys.db.service;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.Role;
import org.ye.psys.db.entity.RoleExample;
import org.ye.psys.db.mapper.RoleMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author liansongye
 * @create 2019-04-16 10:26
 */
@Service
public class RoleService {
    @Autowired
    private RoleMapper roleMapper;

    public Set<String> queryByIds(Integer[] roleIds) {
        Set<String> roles = new HashSet<>();
        if (roleIds.length==0){
            return roles;
        }
        RoleExample example = new RoleExample();
        example.or().andIdIn(Arrays.asList(roleIds)).andEnabledEqualTo(true).andDeletedEqualTo(false);
        List<Role> roleList = roleMapper.selectByExample(example);

        for (Role role : roleList){
            roles.add(role.getName());
        }
        return roles;
    }

    public List<Role> querySelective(String roleName, Integer page, Integer size, String sort, String order) {
        RoleExample example = new RoleExample();
        RoleExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(roleName)) {
            criteria.andNameEqualTo("%" + roleName + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return roleMapper.selectByExample(example);
    }

    public Role findById(Integer id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    public void add(Role role) {
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insertSelective(role);
    }

    public void deleteById(Integer id) {
        roleMapper.logicalDeleteByPrimaryKey(id);
    }

    public void updateById(Role role) {
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateByPrimaryKeySelective(role);
    }

    public boolean checkExist(String name) {
        RoleExample example = new RoleExample();
        example.or().andNameEqualTo(name).andDeletedEqualTo(false);
        return roleMapper.countByExample(example) != 0;
    }

    public List<Role> queryAll() {
        RoleExample example = new RoleExample();
        example.or().andDeletedEqualTo(false);
        return roleMapper.selectByExample(example);
    }
}
