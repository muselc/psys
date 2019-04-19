package org.ye.psys.wxapi.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.service.GoodsService;
import org.ye.psys.wxapi.annotation.LoginUser;

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
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 首页
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public Object index(@LoginUser Integer userId) {
        if (userId == null) {
            userId = 0;
        }
        //判断是否有缓存
        if (redisTemplate.hasKey(userId + "")) {
            Map<Object, Object> dataTemp = redisTemplate.opsForHash().entries(userId + "");
            return ResponseUtil.ok(jsonArrayTOmap(dataTemp));
        }

        Map<String, Object> data = new HashMap<>();

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Callable<List> hotGoodsCallable = () -> goodsService.findByHot(0, 6);
        Callable<List> newGoodsCallable = () -> goodsService.findByNew(0, 6);

        FutureTask<List> hotGoodsListTask = new FutureTask<>(hotGoodsCallable);
        FutureTask<List> newGoodsListTask = new FutureTask<>(newGoodsCallable);

        executorService.submit(hotGoodsListTask);
        executorService.submit(newGoodsListTask);

        try {
            data.put("newGoodsList", newGoodsListTask.get());
            data.put("hotGoodsList", hotGoodsListTask.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        executorService.shutdown();
//缓存数据
        Map<String, Object> dataCache = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            dataCache.put(entry.getKey(),JSON.toJSONString(entry.getValue()));
        }
        redisTemplate.opsForHash().putAll(userId + "", dataCache);
        redisTemplate.expire(userId + "", 5, TimeUnit.MINUTES);
        return ResponseUtil.ok(data);
    }

    /**
     * json字符串转JSONArray
     */
    public JSONObject jsonArrayTOmap(Map<Object, Object> data) {
        JSONObject obj = new JSONObject();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            String str = entry.getValue().toString();
            JSONArray array = JSONArray.parseArray(str);
            obj.put(entry.getKey().toString(), array);
        }
        return obj;
    }
}
