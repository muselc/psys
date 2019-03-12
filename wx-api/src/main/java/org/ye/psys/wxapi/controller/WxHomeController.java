package org.ye.psys.wxapi.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.service.GoodsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


@RestController
@RequestMapping("/wx/home")
public class WxHomeController {
    private final Log logger = LogFactory.getLog(WxHomeController.class);

    @Autowired
    private GoodsService goodsService;

    /**
     * 首页
     */
    @RequestMapping("/index")
    public Object index(Integer userId) {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Map<String, Object> data = new HashMap<>();
        Callable<List> hotGoodsCallable = () -> goodsService.findByHot(0, 6);
        Callable<List> newGoodsCallable = () -> goodsService.findByNew(0, 6);

        FutureTask<List> hotGoodsListTask = new FutureTask<>(hotGoodsCallable);
        FutureTask<List> newGoodsListTask = new FutureTask<>(newGoodsCallable);

        executorService.submit(hotGoodsListTask);
        executorService.submit(newGoodsListTask);

        try{
            data.put("newGoodsList",newGoodsListTask.get());
            data.put("hotGoodsList",hotGoodsListTask.get());
        }catch (Exception e){
            e.printStackTrace();
        }
        //缓存数据
        HomeCacheManager.loadData(HomeCacheManager.INDEX, data);

        executorService.shutdown();
        return ResponseUtil.ok(data);
    }
}
