package org.ye.psys.adminapi.job;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ye.psys.core.config.Kd;
import org.ye.psys.core.config.KdApiOrderDistinguish;
import org.ye.psys.core.util.OrderUtil;
import org.ye.psys.db.entity.Orders;
import org.ye.psys.db.service.OrdersService;
import org.ye.psys.db.service.SystemService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-04-29 15:44
 */
@Component
public class AutoConfirm {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private Kd kd;

    //一小时检测一次
    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void confrim() {
        //已发货订单
        List<Orders> ordersList = ordersService.findByStatus(OrderUtil.STATUS_SHIP);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        KdApiOrderDistinguish api = new KdApiOrderDistinguish(kd);
        for (Orders order : ordersList) {
            try {
                String expNo = order.getShipSn();
                JSONObject dataJson = new JSONObject(api.getOrderTracesByJson(expNo));
                String expCode = dataJson.getJSONArray("Shippers").getJSONObject(0).getString("ShipperCode");

                String result = api.getOrderTracesByJson(expCode, expNo);
                JSONObject data = new JSONObject(result);
                String state = data.getString("State");
                //是否已经签收
                if (Integer.valueOf(state) == 3) {
                    JSONArray traces = data.getJSONArray("Traces");
                    //签收时间
                    String time = traces.getJSONObject(traces.length() - 1).getString("AcceptTime");

                    int maxTime = systemService.findByKName("mall_order_unconfirm");

                    Calendar pre = Calendar.getInstance();
                    pre.setTime(sdf.parse(time));
                    pre.add(Calendar.DATE, maxTime);
                    boolean isBefoe = pre.before(Calendar.getInstance().getTime());
                    //超时设置为402
                    if (isBefoe == false) {
                        order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
                        ordersService.update(order);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
