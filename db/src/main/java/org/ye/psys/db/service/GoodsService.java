package org.ye.psys.db.service;

import org.apache.commons.lang3.StringUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ye.psys.db.entity.Goods;
import org.ye.psys.db.entity.Goods.Column;
import org.ye.psys.db.entity.GoodsExample;
import org.ye.psys.db.entity.GoodsSpecification;
import org.ye.psys.db.mapper.GoodsMapper;
import org.ye.psys.db.mapper.GoodsSpecificationMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoodsService {
    Column[] columns = new Column[]{Column.id, Column.name, Column.detail, Column.picUrl, Column.isHot, Column.isNew, Column.originalPrice, Column.currentPrice};

    @Autowired
    private GoodsMapper goodsMapper;

    public List<Goods> findByHot(int offset, int limit) {
        GoodsExample example = new GoodsExample();
        example.or().andIsHotEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("create_time desc");
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExampleSelective(example, columns);
    }

    public List<Goods> findByNew(int offset, int limit) {
        GoodsExample example = new GoodsExample();
        example.or().andIsNewEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("create_time desc");
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExampleSelective(example, columns);
    }


    public List<Goods> findSelective(Integer categoryId, String keyWord, Integer page, Integer size, String sort, String order) {
        GoodsExample example = new GoodsExample();
        GoodsExample.Criteria criteria1 = example.or();
        GoodsExample.Criteria criteria2 = example.or();

        if (!org.springframework.util.StringUtils.isEmpty(categoryId) && categoryId != 0) {
            criteria1.andCategoryIdEqualTo(categoryId);
            criteria2.andCategoryIdEqualTo(categoryId);
        }

        if (StringUtils.isNotEmpty(keyWord)) {
            criteria1.andKeywordLike("%" + keyWord + "%");
            criteria2.andNameLike("%" + keyWord + "%");
        }
        criteria1.andIsOnSaleEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andIsOnSaleEqualTo(true);
        criteria2.andDeletedEqualTo(false);

        if (StringUtils.isNotEmpty(sort) && StringUtils.isNotEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);

        return goodsMapper.selectByExampleSelective(example, columns);

    }

    public Goods findByGoodsNum(String id) {
        GoodsExample example = new GoodsExample();
        example.or().andGoodsNumEqualTo(id).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return goodsMapper.selectOneByExampleWithBLOBs(example);
    }

    public int count() {
        GoodsExample example = new GoodsExample();
        example.or().andDeletedEqualTo(false);
        return (int) goodsMapper.countByExample(example);
    }

    public List<Goods> querySelective(String goodsSn, String name,String catagory, Integer page, Integer limit, String sort, String order) {
        GoodsExample example = new GoodsExample();
        GoodsExample.Criteria criteria = example.createCriteria();

        if (!org.springframework.util.StringUtils.isEmpty(goodsSn)) {
            criteria.andGoodsNumEqualTo(goodsSn);
        }
        if (!org.springframework.util.StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (!org.springframework.util.StringUtils.isEmpty(catagory)){
            criteria.andCategoryIdEqualTo(Integer.valueOf(catagory));
        }
        criteria.andDeletedEqualTo(false);

        if (!org.springframework.util.StringUtils.isEmpty(sort) && !org.springframework.util.StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return goodsMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteById(Integer gid) {
        goodsMapper.logicalDeleteByPrimaryKey(gid);
    }

    public int updateById(Goods goods) {

        goods.setUpdateTime(LocalDateTime.now());
        return goodsMapper.updateByPrimaryKeySelective(goods);
    }

    public boolean checkExistByName(String name) {
        GoodsExample example = new GoodsExample();
        example.or().andNameEqualTo(name).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return goodsMapper.countByExample(example) != 0;
    }

    public void add(Goods goods) {
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        goodsMapper.insertSelective(goods);
    }

    public boolean checkExistByGoodsNum(String goodsNum) {
        GoodsExample example = new GoodsExample();
        example.or().andGoodsNumEqualTo(goodsNum).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return goodsMapper.countByExample(example) != 0;
    }

    public long countBycateId(Integer id) {
        GoodsExample example = new GoodsExample();
        example.or().andCategoryIdEqualTo(id).andDeletedEqualTo(false);
        return goodsMapper.countByExample(example);
    }
}
