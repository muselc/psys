package org.ye.psys.db.service;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.Area;
import org.ye.psys.db.entity.AreaExample;
import org.ye.psys.db.mapper.AreaMapper;

import java.util.List;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-03-25 10:55
 */
@Service
public class AreaService {
    @Autowired
    private AreaMapper areaMapper;

    public Area findById(Integer id) {
        return areaMapper.selectByPrimaryKey(id);
    }

    public List<Area> findByPid(Integer id) {
        AreaExample example = new AreaExample();
        example.or().andPidEqualTo(id);
        return areaMapper.selectByExample(example);
    }

    public List<Area> querySelective(String name, Integer code, Integer page, Integer limit, String sort, String order) {
        AreaExample example = new AreaExample();
        AreaExample.Criteria criteria = example.or();
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameEqualTo(name);
        }
        if (code != null && code != 0) {
            criteria.andCodeEqualTo(code);
        }
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }
        PageHelper.startPage(page, limit);
        return areaMapper.selectByExample(example);
    }


    public List<Area> findByType(int type) {
        AreaExample example = new AreaExample();
        example.or().andTypeEqualTo((byte) type);
        return areaMapper.selectByExample(example);
    }


    public void updateValue(Area area) {
        areaMapper.updateByPrimaryKey(area);
        return ;
    }

    public void updateValueById(int id, int value) {
        areaMapper.updateValueById(id,value);
    }
}
