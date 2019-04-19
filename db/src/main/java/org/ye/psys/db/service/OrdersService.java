package org.ye.psys.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.Orders;
import org.ye.psys.db.entity.OrdersExample;
import org.ye.psys.db.mapper.OrdersMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @Author liansongye
 * @create 2019-03-20 15:19
 */
@Service
public class OrdersService {
    @Autowired
    private OrdersMapper ordersMapper;


    public List<Orders> findByStatus(Integer userId, List<Short> orderStatus) {
        OrdersExample example = new OrdersExample();
        OrdersExample.Criteria criteria = example.or();
        criteria.andUserIdEqualTo(userId);
        if (orderStatus!=null){
            criteria.andOrderStatusIn(orderStatus);
        }
        criteria.andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        return ordersMapper.selectByExample(example);
    }

    public String generateOrderSn() {
            int machineId = 1;//最大支持1-9个集群机器部署
            int hashCodev = UUID.randomUUID().toString().hashCode();
            if(hashCodev < 0){
                //有可能是负数
                hashCodev = -hashCodev;
            }
            //"%015d"的意思：0代表不足位数的补0，这样可以确保相同的位数，15是位数也就是要得到到的字符串长度是15，d代表数字。
            return machineId + String.format("%015d", hashCodev);

    }

    public int add(Orders order) {
        order.setAddTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return ordersMapper.insertSelective(order);
    }

    public Orders findById(Integer orderId) {
        return ordersMapper.selectByPrimaryKey(orderId);
    }

    public int update(Orders order) {
        order.setUpdateTime(LocalDateTime.now());
        return ordersMapper.updateByPrimaryKey(order);
    }

    public int count() {
        OrdersExample example = new OrdersExample();
        example.or().andDeletedEqualTo(false);
        return (int) ordersMapper.countByExample(example);
    }
}