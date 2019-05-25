package org.ye.psys.adminapi.controller;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.entity.Goods;
import org.ye.psys.db.entity.UserChart;
import org.ye.psys.db.mapper.OrderGoodsMapper;
import org.ye.psys.db.service.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author liansongye
 * @create 2019-04-19 09:19
 */
@RestController
@RequestMapping("/admin/dashboard")
public class DashbordController {

    private final Log logger = LogFactory.getLog(DashbordController.class);

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsStockService stockService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private UserChartService userChartService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderGoodsMapper orderGoodsMapper;

    @GetMapping("/info")
    public Object info() {

        int userOnTotal = userService.count();
        int goodsTotal = goodsService.count();
        int stockTotal = stockService.count();
        int orderTotal = ordersService.count();

        Map<String, Map<String, List<Object>>> lineChartData = new HashedMap();
        Map<String, List<Object>> visiter = new HashedMap();
        List<Object> expectedData = new ArrayList<>();
        List<Object> actualData = new ArrayList<>();
        List<Object> names = new ArrayList<>();
        List<UserChart> userChartList = userChartService.queryAll();

        long end = System.currentTimeMillis();

        long start = System.currentTimeMillis();

        List list = new ArrayList();
        for (int i = 1; i <= 7; i++) {
            start = start - i * 86400000L;
            String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end);
            String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start);

            list = (orderGoodsMapper.queryByGoodsSnL3(startTime, endTime));
            Map map = (Map) list.get(0);
            String goodsN = (String) map.get("goods_sn");
            Goods goods = goodsService.findByGoodsNum(goodsN);
            BigDecimal total = (BigDecimal) map.get("total");
            expectedData.add(0);
            actualData.add(total.intValue());
//            names.add(goods.getName());
            names.add(goodsN);
            visiter.put("expectedData", expectedData);
            visiter.put("actualData", actualData);
            visiter.put("names",names);
            lineChartData.put("visiter", visiter);
        }
        lineChartData.put("visiter", visiter);
//        Date today = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(today);
//        int weekday = c.get(Calendar.DAY_OF_WEEK);
//
//        if (weekday == 1) {
//            weekday = 7;
//        } else {
//            weekday -= 1;
//        }
//
//        for (int i = 0; i < weekday; i++) {
//            expectedData.add(userChartList.get(i).getOnline());
//            actualData.add(userChartList.get(i).getTotal());
//        }
//        visiter.put("expectedData", expectedData);
//        visiter.put("actualData", actualData);
//        lineChartData.put("visiter", visiter);

        Map data = new HashMap<>();
        data.put("userOnTotal", userOnTotal);
        data.put("goodsTotal", goodsTotal);
        data.put("stockTotal", stockTotal);
        data.put("orderTotal", orderTotal);
        data.put("lineChartData", lineChartData);
        return ResponseUtil.ok(data);
    }

}
