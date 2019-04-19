package org.ye.psys.wxapi.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.core.validator.Order;
import org.ye.psys.core.validator.Sort;
import org.ye.psys.db.entity.Category;
import org.ye.psys.db.entity.Goods;
import org.ye.psys.db.entity.GoodsStock;
import org.ye.psys.db.entity.SearchHistory;
import org.ye.psys.db.service.*;
import org.ye.psys.wxapi.annotation.LoginUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("wx/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SearchHistoryService searchHistoryService;

    @Autowired
    private GoodsSpecificationService goodsSpecificationService;

    @Autowired
    private GoodsStockService goodsStockService;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    @RequestMapping(value = "/category", method = RequestMethod.GET)
    public Object category(@RequestParam("id") Integer id) {
        Category cur = categoryService.findById(id);
        Category parent = categoryService.findById(cur.getPid());
        List<Category> secondList = categoryService.findByPid(cur.getPid());
        Map<String, Object> data = new HashMap<>();
        data.put("curSecond", cur);
        data.put("parent", parent);
        data.put("secondList", secondList);

        return ResponseUtil.ok(data);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Object goodsList(Integer categoryId,
                            String keyWord,
                            @LoginUser Integer userId,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer size,
                            @Sort(accepts = {"create_time", "name", "current_price"}) @RequestParam(defaultValue = "create_time") String sort,
                            @Order @RequestParam(defaultValue = "desc") String order
    ) {
        if (null != userId && 0!=userId && StringUtils.isNotBlank(keyWord)) {
            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setKeyword(keyWord);
            searchHistory.setUserId(userId);
            //判断记录是否已经存在
            SearchHistory isExit = searchHistoryService.findByUserIdAndKey(userId, keyWord);
            if (null == isExit) {
                searchHistoryService.save(searchHistory);
            }
        }

        List<Goods> goodsList = goodsService.findSelective(categoryId, keyWord, page, size, sort, order);

        Map<String, Object> data = new HashMap<>();
        data.put("goodsList", goodsList);

        return ResponseUtil.ok(data);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public Object detail(@RequestParam("id") String id) {
        Goods info = goodsService.findByGoodsNum(id);
        //商品规格
        Callable<Object> objectCallable = () -> goodsSpecificationService.getSpecificationList(id);
        //商品规格对应对库存和价格
        Callable<List> stockCallable = () -> goodsStockService.findByGoodsNum(id);
        FutureTask<Object> objectCallableTask = new FutureTask<>(objectCallable);

        FutureTask<List> stockCallableTask = new FutureTask<>(stockCallable);

        executorService.submit(objectCallableTask);
        executorService.submit(stockCallableTask);
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("info", info);
            data.put("specificationList", objectCallableTask.get());
            data.put("stockList", stockCallableTask.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok(data);
    }

    @RequestMapping(value = "/change", method = RequestMethod.GET)
    public Object change(
            @RequestParam String goodId,
            @RequestParam List<String> valueList) {
        String[] values = new String[valueList.size()];
        for (int i = 0; i < valueList.size(); i++) {
            values[i] = valueList.get(i);
        }
        GoodsStock goodsStock = goodsStockService.findBySpecifications(goodId, values);

        Map<String, Object> data = new HashMap<>();
        data.put("goods", goodsStock);
        return ResponseUtil.ok(data);
    }

}
