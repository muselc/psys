package org.ye.psys.adminapi.controller;

import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ye.psys.adminapi.annotation.RequiresPermissionsDesc;
import org.ye.psys.adminapi.dao.GoodsAllinone;
import org.ye.psys.adminapi.service.AdminGoodsService;
import org.ye.psys.core.util.JacksonUtil;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.core.validator.Order;
import org.ye.psys.core.validator.Sort;
import org.ye.psys.db.entity.*;
import org.ye.psys.db.service.*;

import javax.validation.constraints.NotNull;
import java.lang.System;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/goods")
@Validated
public class AdminGoodsController {
    private final Log logger = LogFactory.getLog(AdminGoodsController.class);

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsSpecificationService goodsSpecificationService;
    @Autowired
    private GoodsStockService goodsStockService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AdminGoodsService adminGoodsService;
    @Autowired
    private OrderGoodsService orderGoodsService;


    /**
     * 查询商品
     *
     * @param goodsNum
     * @param name
     * @param catagory
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @RequiresPermissions("admin:goods:list")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String goodsNum, String name, String catagory,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "create_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<Goods> goodsList = goodsService.querySelective(goodsNum, name, catagory, page, limit, sort, order);
        long total = PageInfo.of(goodsList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", goodsList);

        return ResponseUtil.ok(data);
    }


    /**
     * 编辑商品
     *
     * @param goodsAllinone
     * @return
     */
    @RequiresPermissions("admin:goods:update")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody GoodsAllinone goodsAllinone) {
        return adminGoodsService.update(goodsAllinone);
    }

    /**
     * 删除商品
     *
     * @param goods
     * @return
     */
    @RequiresPermissions("admin:goods:delete")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody Goods goods) {
        Integer id = goods.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        Integer gid = goods.getId();
        goodsService.deleteById(gid);
        goodsSpecificationService.deleteByGid(gid);
        goodsStockService.deleteByGid(gid);
        return ResponseUtil.ok();
    }

    /**
     * 添加商品
     *
     * @param goodsAllinone
     * @return
     */
    @RequiresPermissions("admin:goods:create")
    @RequiresPermissionsDesc(menu = {"商场管理", "商品列表"}, button = "上架")
    @PostMapping("/create")
    public Object create(@RequestBody GoodsAllinone goodsAllinone) {
        return adminGoodsService.create(goodsAllinone);
    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:goods:read")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        Goods goods = goodsService.findByGoodsNum(id.toString());
        List<GoodsStock> stocks = goodsStockService.findByGoodsNum(id.toString());
        List<GoodsSpecification> specifications = goodsSpecificationService.queryByGoodsNum(id.toString());

        Integer categoryId = goods.getCategoryId();
        Category category = categoryService.findById(categoryId);
        Integer[] categoryIds = new Integer[]{};
        if (category != null) {
            Integer parentCategoryId = category.getPid();
            categoryIds = new Integer[]{parentCategoryId, categoryId};
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goods", goods);
        data.put("specifications", specifications);
        data.put("stocks", stocks);
        data.put("categoryIds", categoryIds);

        return ResponseUtil.ok(data);
    }

    @GetMapping("/cat")
    public Object list2() {
        return adminGoodsService.list2();
    }

    @RequiresPermissions("admin:goods:data")
    @RequiresPermissionsDesc(menu = {"参谋", "数据分析"}, button = "排行榜")
    @PostMapping("/data")
    public Object data(@RequestBody String body) {
        Integer isSingle = JacksonUtil.parseInteger(body, "isSingle");
        Integer time = JacksonUtil.parseInteger(body, "time");
        if (isSingle == null) {
            isSingle = 3;
        }
        if (time == null) {
            time = 15;
        }
        List list = new ArrayList();

        long end = System.currentTimeMillis();

        long start = end - time * 86400000L;

        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start);

        //前10
        if (isSingle == 1) {
            list = orderGoodsService.queryByGoodsSnL1(startTime, endTime);
        } else if (isSingle == 2) {
            list = orderGoodsService.queryByGoodsSnL2(startTime, endTime);

        } else if (isSingle == 3) {
            list = orderGoodsService.queryByGoodsSnL3(startTime, endTime);
            List nList = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                String goodsN = (String) map.get("goods_sn");
                BigDecimal total = (BigDecimal) map.get("total");
                Goods goods = goodsService.findByGoodsNum(goodsN);
                Map temp = new HashedMap();
                temp.put("rank", i + 1);
                temp.put("newName", goods.getName());
                temp.put("total", total);
                nList.add(temp);
            }
            list = nList;
        }
        Map<String, Object> data = new HashedMap();
        data.put("list", list);
        return ResponseUtil.ok(data);
    }



}
