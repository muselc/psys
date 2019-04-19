package org.ye.psys.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.Admin;
import org.ye.psys.db.entity.AdminExample;
import org.ye.psys.db.mapper.AdminMapper;

import java.util.List;

/**
 * @Author liansongye
 * @create 2019-04-16 10:25
 */
@Service
public class AdminService {
    @Autowired
    private AdminMapper adminMapper;

    public List<Admin> findByUsername(String username) {
        AdminExample example = new AdminExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return adminMapper.selectByExample(example);
    }
}
