package org.ye.psys.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.UserChart;
import org.ye.psys.db.entity.UserChartExample;
import org.ye.psys.db.mapper.UserChartMapper;

import java.util.List;

/**
 * @Author liansongye
 * @create 2019-05-14 13:42
 */
@Service
public class UserChartService {
    @Autowired
    private UserChartMapper userChartMapper;

    public void update(UserChart userChart) {
        userChartMapper.updateByPrimaryKey(userChart);
        return;
    }

    public List<UserChart> queryAll() {
        UserChartExample example = new UserChartExample();
        example.or().andIdIsNotNull();
        return userChartMapper.selectByExample(example);
    }
}
