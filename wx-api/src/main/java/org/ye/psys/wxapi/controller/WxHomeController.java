package org.ye.psys.wxapi.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


@RestController
@RequestMapping("/wx/home")
public class WxHomeController {
    private final Log logger = LogFactory.getLog(WxHomeController.class);

    /**
     * 首页
     */
    @RequestMapping("/index")
    public Object index(Integer userId){

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Map<String, Object> data = new HashMap<>();
        return  new Object();
    }
}
