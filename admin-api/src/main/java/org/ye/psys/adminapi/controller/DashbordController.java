package org.ye.psys.adminapi.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.service.GoodsService;
import org.ye.psys.db.service.GoodsStockService;
import org.ye.psys.db.service.OrdersService;
import org.ye.psys.db.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-04-19 09:19
 */
@RestController
@RequestMapping("/admin/dashboard")
public class DashbordController {

    private final Log logger = LogFactory.getLog(DashbordController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsStockService stockService;
    @Autowired
    private OrdersService ordersService;

    @GetMapping("")
    public Object info() {
        int userOnTotal = userService.countOn();
        int userTotal = userService.count();
        int goodsTotal = goodsService.count();
        int stockTotal = stockService.count();
        int orderTotal = ordersService.count();
//        List<Map<String, List<Integer>>> lineChartData = new ArrayList<>(4);
//
//
//        for (int i = 0; i < 2; i++) {
//            Map<String, List<Integer>> chartData = new HashMap<>(2);
//            for (int j = 0; j < 12; j++) {
//                List<Integer> expectedData = new ArrayList<>(12);
//                expectedData.add
//            }
//        }
        Map data = new HashMap<>();
        data.put("userOnTotal", userOnTotal);
        data.put("userTotal", userTotal);
        data.put("goodsTotal", goodsTotal);
        data.put("stockTotal", stockTotal);
        data.put("orderTotal", orderTotal);
//        data.put("lineChartData", lineChartData);
        return ResponseUtil.ok(data);
    }

}
