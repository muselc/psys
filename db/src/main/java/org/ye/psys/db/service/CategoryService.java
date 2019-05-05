package org.ye.psys.db.service;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ye.psys.db.entity.Category;
import org.ye.psys.db.entity.CategoryExample;
import org.ye.psys.db.mapper.CategoryMapper;
import org.ye.psys.db.entity.Category.Column;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    private Column[] CHANNEL = {Column.id, Column.name, Column.picUrl};

    /**
     * 一级目录列表
     */
    public List<Category> findL1() {
        CategoryExample example = new CategoryExample();
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
        return categoryMapper.selectByExample(example);
    }

    /**
     * 二级目录列表
     *
     * @param pid
     */
    public List<Category> findByPid(Integer pid) {
        CategoryExample example = new CategoryExample();
        example.or().andPidEqualTo(pid).andDeletedEqualTo(false);
        return categoryMapper.selectByExample(example);
    }

    /**
     * 根据id查询目录
     *
     * @param id
     */
    public Category findById(Integer id) {
        CategoryExample example = new CategoryExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return categoryMapper.selectByPrimaryKey(id);
    }

    public List<Category> querySelective(String id, String name, Integer page, Integer limit, String sort, String order) {
        CategoryExample example = new CategoryExample();
        CategoryExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return categoryMapper.selectByExample(example);
    }

    public void add(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insertSelective(category);
    }

    public int updateById(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        return categoryMapper.updateByPrimaryKeySelective(category);
    }

    public void deleteById(Integer id) {
        categoryMapper.logicalDeleteByPrimaryKey(id);
    }
}
