package org.ye.psys.adminapi.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ye.psys.adminapi.annotation.RequiresPermissionsDesc;
import org.ye.psys.core.storage.StoragesService;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.entity.Storage;
import org.ye.psys.db.service.StorageService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-04-24 11:04
 */
@RestController
@RequestMapping("/admin/storage")
public class StorageController {

    @Autowired
    private StoragesService storagesService;
    @Autowired
    private StorageService storageService;

    @RequiresPermissions("admin:storage:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "对象存储"}, button = "上传")
    @PostMapping("/create")
    public Object create(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String url = storagesService.store(file.getInputStream(), file.getSize(), file.getContentType(), originalFilename);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:storage:update")
    @RequiresPermissionsDesc(menu={"系统管理" , "对象存储"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody Storage Storage) {
        if (storageService.update(Storage) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(Storage);
    }

    @RequiresPermissions("admin:storage:delete")
    @RequiresPermissionsDesc(menu={"系统管理" , "对象存储"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody Storage Storage) {
        String key = Storage.getKey();
        if (StringUtils.isEmpty(key)) {
            return ResponseUtil.badArgument();
        }
        storageService.deleteByKey(key);
        storagesService.delete(key);
        return ResponseUtil.ok();
    }

}
