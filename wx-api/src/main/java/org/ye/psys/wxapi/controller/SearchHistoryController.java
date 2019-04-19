package org.ye.psys.wxapi.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.entity.Goods;
import org.ye.psys.db.entity.SearchHistory;
import org.ye.psys.db.service.GoodsService;
import org.ye.psys.db.service.SearchHistoryService;
import org.ye.psys.wxapi.annotation.LoginUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-03-18 15:26
 */
@RequestMapping("wx/search")
@RestController
public class SearchHistoryController {

    @Autowired
    private SearchHistoryService searchHistoryService;

    @Autowired
    private GoodsService goodsService;

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public Object getHistory(@LoginUser Integer userId) {
        List<SearchHistory> searchHistory = searchHistoryService.findByUserId(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("history", searchHistory);
        return ResponseUtil.ok(data);
    }

    @RequestMapping(value = "/keyWord", method = RequestMethod.GET)
    public Object findByKeyWord(@RequestParam("keyWord") String keyWord) {
        List<Goods> goodsList = goodsService.findSelective(null, keyWord, 1, 7, "", "");

        Map<String, Object> data = new HashMap<>();
        data.put("goodsList", goodsList);
        return ResponseUtil.ok(data);
    }

    @RequestMapping(value = "/delete/one", method = RequestMethod.DELETE)
    public Object deleteHistory(@LoginUser Integer userId,
                                @RequestParam(value = "keyWord") String keyWord) {
        if (StringUtils.isEmpty(keyWord) || null == userId) {
            return ResponseUtil.fail();
        }
        searchHistoryService.deleteHistory(userId, keyWord);
        return ResponseUtil.ok();
    }

    @RequestMapping(value = "/delete/all", method = RequestMethod.DELETE)
    public Object deleteAllHistory(@LoginUser Integer userId) {
        if (null == userId) {
            return ResponseUtil.fail();
        }
        searchHistoryService.deleteAllHistory(userId);
        return ResponseUtil.ok();
    }
}
