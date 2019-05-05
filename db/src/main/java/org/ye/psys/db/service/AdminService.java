package org.ye.psys.db.service;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ye.psys.db.entity.Admin;
import org.ye.psys.db.entity.Admin.Column;
import org.ye.psys.db.entity.AdminExample;
import org.ye.psys.db.mapper.AdminMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-04-16 10:25
 */
@Service
public class AdminService {
    private final Column[] result = new Column[]{Column.id, Column.username,Column.password, Column.avatar, Column.roleIds};

    @Autowired
    private AdminMapper adminMapper;

    public List<Admin> findByUsername(String username) {
        AdminExample example = new AdminExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return adminMapper.selectByExample(example);
    }
    public List<Admin> querySelective(String username, Integer page, Integer limit, String sort, String order) {
        AdminExample example = new AdminExample();
        AdminExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return adminMapper.selectByExampleSelective(example, result);
    }

    public int updateById(Admin admin) {
        admin.setUpdateTime(LocalDateTime.now());
        return adminMapper.updateByPrimaryKeySelective(admin);
    }

    public void deleteById(Integer id) {
        adminMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(Admin admin) {
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.insertSelective(admin);
    }

    public Admin findById(Integer id) {
        return adminMapper.selectByPrimaryKeySelective(id, result);
    }
}

