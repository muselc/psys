package org.ye.psys.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.OrderGoods;
import org.ye.psys.db.entity.OrderGoodsExample;
import org.ye.psys.db.mapper.OrderGoodsMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-03-20 16:35
 */
@Service
public class OrderGoodsService {

    @Autowired
    private OrderGoodsMapper orderGoodsMapper;

    public List<OrderGoods> findById(Integer id) {
        OrderGoodsExample example = new OrderGoodsExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return orderGoodsMapper.selectByExample(example);
    }

    public int add(OrderGoods orderGoods) {
        orderGoods.setCreateTime(LocalDateTime.now());
        orderGoods.setUpdateTime(LocalDateTime.now());
        return orderGoodsMapper.insertSelective(orderGoods);
    }

    public List<OrderGoods> findByOrderId(Integer id) {
        OrderGoodsExample example = new OrderGoodsExample();
        example.or().andOrderIdEqualTo(id).andDeletedEqualTo(false);
        return  orderGoodsMapper.selectByExample(example);
    }

    public List<OrderGoods> findByGoodsSn(String goodsSn) {
        OrderGoodsExample example = new OrderGoodsExample();
        example.or().andGoodsSnEqualTo(goodsSn).andDeletedEqualTo(false);
        return orderGoodsMapper.selectByExample(example);
    }
}
