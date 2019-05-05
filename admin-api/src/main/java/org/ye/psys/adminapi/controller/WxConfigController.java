package org.ye.psys.adminapi.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ye.psys.adminapi.annotation.RequiresPermissionsDesc;
import org.ye.psys.core.system.SystemConfig;
import org.ye.psys.core.util.JacksonUtil;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.service.SystemService;

import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-05-04 13:04
 */
@RestController
@RequestMapping("/admin/config")
@Validated
public class WxConfigController {

    private final Log logger = LogFactory.getLog(WxConfigController.class);

    @Autowired
    private SystemService systemService;

    @RequiresPermissions("admin:config:express:list")
    @RequiresPermissionsDesc(menu = {"商场配置", "运费配置"}, button = "详情")
    @GetMapping("/express")
    public Object listExpress() {
        Map<String, String> data = systemService.listExpress();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:express:updateConfigs")
    @RequiresPermissionsDesc(menu = {"商场配置", "运费配置"}, button = "编辑")
    @PostMapping("/express")
    public Object updateExpress(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:config:order:list")
    @RequiresPermissionsDesc(menu = {"商场配置", "订单配置"}, button = "详情")
    @GetMapping("/order")
    public Object lisOrder() {
        Map<String, String> data = systemService.listOrder();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:order:updateConfigs")
    @RequiresPermissionsDesc(menu = {"商场配置", "订单配置"}, button = "编辑")
    @PostMapping("/order")
    public Object updateOrder(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemService.updateConfig(data);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:config:wx:list")
    @RequiresPermissionsDesc(menu = {"商场配置", "小程序配置"}, button = "详情")
    @GetMapping("/wx")
    public Object listWx() {
        Map<String, String> data = systemService.listWx();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:wx:updateConfigs")
    @RequiresPermissionsDesc(menu = {"商场配置", "小程序配置"}, button = "编辑")
    @PostMapping("/wx")
    public Object updateWx(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }
}


