package org.ye.psys.wxapi.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.entity.Area;
import org.ye.psys.db.service.AreaService;

import java.util.List;

/**
 * @Author liansongye
 * @create 2019-03-25 15:18
 */
@RequestMapping("/wx/area")
@RestController
public class AreaController {
    private final Log logger = LogFactory.getLog(AreaController.class);

    @Autowired
    private AreaService areaService;

   @RequestMapping("/list")
    public Object list(@RequestParam("pid") Integer pid){
        List<Area> areaList = areaService.findByPid(pid);
        return ResponseUtil.ok(areaList);
    }
}
