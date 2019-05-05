package org.ye.psys.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.GoodsStock;
import org.ye.psys.db.entity.GoodsStockExample;
import org.ye.psys.db.mapper.GoodsStockMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-03-27 15:28
 */
@Service
public class GoodsStockService {
    @Autowired
    private GoodsStockMapper goodsStockMapper;

    public List findByGoodsNum(String id) {
        GoodsStockExample example = new GoodsStockExample();
        example.or().andGoodsNumEqualTo(id);
        return goodsStockMapper.selectByExample(example);
    }

    public GoodsStock findBySpecifications(String goodsId, String[] values) {
        GoodsStockExample example = new GoodsStockExample();
        example.or().andSpecificationsEqualTo(values).andGoodsNumEqualTo(goodsId);
        return goodsStockMapper.selectOneByExample(example);
    }

    public GoodsStock findById(Integer stockId) {
        return goodsStockMapper.selectByPrimaryKey(stockId);
    }

    public int reduceNum(Integer stockId, int left) {
        return goodsStockMapper.reduceStock(stockId, left);
    }

    public int count() {
        GoodsStockExample example = new GoodsStockExample();
        example.or().andDeletedEqualTo(false);
        return (int) goodsStockMapper.countByExample(example);
    }

    public void deleteByGid(Integer gid) {
        GoodsStockExample example = new GoodsStockExample();
        example.or().andGoodsNumEqualTo(gid.toString());
        goodsStockMapper.logicalDeleteByExample(example);
    }

    public void add(GoodsStock product) {
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        goodsStockMapper.insertSelective(product);
    }

    public int addStock(Integer stockId, int number) {
        return goodsStockMapper.addStock(stockId, number);
    }
}
