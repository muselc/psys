package org.ye.psys.adminapi.controller;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.adminapi.annotation.RequiresPermissionsDesc;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.core.validator.Order;
import org.ye.psys.core.validator.Sort;
import org.ye.psys.db.entity.Address;
import org.ye.psys.db.entity.Area;
import org.ye.psys.db.service.AddressService;
import org.ye.psys.db.service.AreaService;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-04-19 14:29
 */
@RestController
@RequestMapping("/admin/area")
public class AreaController {
    private final Log logger = LogFactory.getLog(AreaController.class);

    @Autowired
    private AreaService areaService;

    @GetMapping("/clist")
    public Object clist(@NotNull Integer id) {
        List<Area> regionList = areaService.findByPid(id);
        return ResponseUtil.ok(regionList);
    }

    @RequiresPermissions("admin:area:list")
    @RequiresPermissionsDesc(menu={"商城管理" , "行政区域"}, button="查询")
    @GetMapping("/list")
    public Object list(String name, Integer code,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort(accepts = {"id"}) @RequestParam(defaultValue = "id") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<Area> regionList = areaService.querySelective(name, code, page, limit, sort, order);
        long total = PageInfo.of(regionList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", regionList);

        return ResponseUtil.ok(data);
    }
    @RequiresPermissions("admin:area:areaData")
    @RequiresPermissionsDesc(menu={"参谋" , "数据分析"}, button="区域分布")
    @GetMapping("/areaData")
    public Object areaData() {
        List<Map> areaList = new ArrayList<>();

        List<Area> a = areaService.findByType(1);
        for (Area area : a) {
            Map<String, String> areaData = new HashMap<>();
            areaData.put("name", area.getName());
            areaData.put("value", area.getValue().toString());
            areaList.add(areaData);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("area", areaList);
        return ResponseUtil.ok(data);
    }

}
