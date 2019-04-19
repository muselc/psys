package org.ye.psys.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.UserFormid;
import org.ye.psys.db.mapper.UserFormidMapper;

import java.time.LocalDateTime;

/**
 * @Author liansongye
 * @create 2019-04-10 14:22
 */
@Service
public class UserFormidService {
    @Autowired
    private UserFormidMapper userFormidMapper;

    public void add(UserFormid userFormid) {
        userFormid.setAddTime(LocalDateTime.now());
        userFormid.setUpdateTime(LocalDateTime.now());
        userFormidMapper.insertSelective(userFormid);
    }
}
