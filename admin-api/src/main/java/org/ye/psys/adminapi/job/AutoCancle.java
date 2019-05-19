package org.ye.psys.adminapi.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ye.psys.core.util.OrderUtil;
import org.ye.psys.db.entity.Orders;
import org.ye.psys.db.service.OrdersService;
import org.ye.psys.db.service.SystemService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-05-05 10:48
 */
@Component
public class AutoCancle {
    @Autowired
    private SystemService systemService;
    @Autowired
    private OrdersService ordersService;

    //一分钟检测一次
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void cancle() {
        //获取未付款订单
        List<Orders> ordersList = ordersService.findByStatus(OrderUtil.STATUS_CREATE);

        if (ordersList.size() > 0) {
            //获取时限
            int maxTime = (int) systemService.findByKName("mall_order_unpaid");
            for (Orders order : ordersList) {
                Long nowTime = Calendar.getInstance().getTimeInMillis();

                Long preTime = order.getCreateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli();
                if ((nowTime - preTime) >= 60 * 60 * maxTime) {
                    order.setOrderStatus(OrderUtil.STATUS_CANCEL);
                    ordersService.update(order);
                }

            }
        }
    }

}
