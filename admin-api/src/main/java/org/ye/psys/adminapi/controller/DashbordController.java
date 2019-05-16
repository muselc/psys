package org.ye.psys.adminapi.controller;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.core.util.UserTokenManager;
import org.ye.psys.db.entity.UserChart;
import org.ye.psys.db.service.*;

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

    @GetMapping("/info")
    public Object info() {
        int userOnTotal = UserTokenManager.count();
        int goodsTotal = goodsService.count();
        int stockTotal = stockService.count();
        int orderTotal = ordersService.count();

        Map<String, Map<String,List<Integer>>> lineChartData = new HashedMap();
        Map<String,List<Integer>> visiter = new HashedMap();
        List<Integer> expectedData = new ArrayList<>();
        List<Integer> actualData = new ArrayList<>();
        List<UserChart> userChartList = userChartService.queryAll();

        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int weekday = c.get(Calendar.DAY_OF_WEEK);

        if (weekday == 1) {
            weekday = 7;
        } else {
            weekday -= 1;
        }

        for (int i = 0;i<weekday;i++){
            expectedData.add(userChartList.get(i).getOnline());
            actualData.add(userChartList.get(i).getTotal());
        }
        visiter.put("expectedData",expectedData);
        visiter.put("actualData",actualData);
        lineChartData.put("visiter",visiter);

        Map data = new HashMap<>();
        data.put("userOnTotal", userOnTotal);
        data.put("goodsTotal", goodsTotal);
        data.put("stockTotal", stockTotal);
        data.put("orderTotal", orderTotal);
        data.put("lineChartData", lineChartData);
        return ResponseUtil.ok(data);
    }

}
