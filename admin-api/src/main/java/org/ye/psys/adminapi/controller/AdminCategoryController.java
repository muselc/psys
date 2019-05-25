package org.ye.psys.adminapi.controller;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ye.psys.adminapi.annotation.RequiresPermissionsDesc;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.core.validator.Order;
import org.ye.psys.core.validator.Sort;
import org.ye.psys.db.entity.Category;
import org.ye.psys.db.service.CategoryService;
import org.ye.psys.db.service.GoodsService;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/category")
@Validated
public class AdminCategoryController {
    private final Log logger = LogFactory.getLog(AdminCategoryController.class);

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private GoodsService goodsService;

    @RequiresPermissions("admin:category:list")
    @RequiresPermissionsDesc(menu={"商场管理" , "类目管理"}, button="查询")
    @GetMapping("/list")
    public Object list(String id, String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "create_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<Category> collectList = categoryService.querySelective(id, name, page, limit, sort, order);
        long total = PageInfo.of(collectList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", collectList);

        return ResponseUtil.ok(data);
    }

    private Object validate(Category category) {
        String name = category.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        String level = category.getLevel();
        if (StringUtils.isEmpty(level)) {
            return ResponseUtil.badArgument();
        }
        if (!level.equals("L1") && !level.equals("L2")) {
            return ResponseUtil.badArgumentValue();
        }

        Integer pid = category.getPid();
        if (level.equals("L2") && (pid == null)) {
            return ResponseUtil.badArgument();
        }

        return null;
    }

    @RequiresPermissions("admin:category:create")
    @RequiresPermissionsDesc(menu={"商场管理" , "类目管理"}, button="添加")
    @PostMapping(value = "/create")
    public Object create(@RequestBody Category category) {
        Object error = validate(category);
        if (error != null) {
            return error;
        }
        categoryService.add(category);
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:category:read")
    @RequiresPermissionsDesc(menu={"商场管理" , "类目管理"}, button="详情")
    @GetMapping(value = "/read")
    public Object read(@NotNull Integer id) {
        Category category = categoryService.findById(id);
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:category:update")
    @RequiresPermissionsDesc(menu={"商场管理" , "类目管理"}, button="编辑")
    @PostMapping(value = "/update")
    public Object update(@RequestBody Category category) {
        Object error = validate(category);
        if (error != null) {
            return error;
        }

        if (categoryService.updateById(category) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:category:delete")
    @RequiresPermissionsDesc(menu={"商场管理" , "类目管理"}, button="删除")
    @PostMapping(value = "/delete")
    public Object delete(@RequestBody Category category) {
        Integer id = category.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        //一级目录
        if (category.getLevel().equals("L1")) {
            List<Category> categoryList = categoryService.findByPid(id);
            if (categoryList.size() > 0) {
                return ResponseUtil.fail("请先删除上级类目");
            }
        }
        //二级目录
        if (category.getLevel().equals("L2")) {
            long count = goodsService.countBycateId(id);
            if (count > 0) {
                return ResponseUtil.fail("请先删除该类目下的商品");
            }
        }
        categoryService.deleteById(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:category:list")
    @GetMapping(value = "/l1")
    public Object catL1() {
        // 所有一级分类目录
        List<Category> l1CatList = categoryService.findL1();
        List<Map<String, Object>> data = new ArrayList<>(l1CatList.size());
        for (Category category : l1CatList) {
            Map<String, Object> d = new HashMap<>(2);
            d.put("value", category.getId());
            d.put("label", category.getName());
            data.add(d);
        }
        return ResponseUtil.ok(data);
    }
}
