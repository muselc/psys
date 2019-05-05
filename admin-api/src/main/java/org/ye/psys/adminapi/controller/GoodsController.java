package org.ye.psys.adminapi.controller;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ye.psys.adminapi.annotation.RequiresPermissionsDesc;
import org.ye.psys.adminapi.dao.GoodsAllinone;
import org.ye.psys.adminapi.service.AdminGoodsService;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.core.validator.Order;
import org.ye.psys.core.validator.Sort;
import org.ye.psys.db.entity.Category;
import org.ye.psys.db.entity.Goods;
import org.ye.psys.db.entity.GoodsSpecification;
import org.ye.psys.db.entity.GoodsStock;
import org.ye.psys.db.service.CategoryService;
import org.ye.psys.db.service.GoodsService;
import org.ye.psys.db.service.GoodsSpecificationService;
import org.ye.psys.db.service.GoodsStockService;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/goods")
@Validated
public class GoodsController {
    private final Log logger = LogFactory.getLog(GoodsController.class);

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


    /**
     * 查询商品
     *
     * @param goodsSn
     * @param name
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @RequiresPermissions("admin:goods:list")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String goodsSn, String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "create_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<Goods> goodsList = goodsService.querySelective(goodsSn, name, page, limit, sort, order);
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
        List<GoodsStock> Stocks = goodsStockService.findByGoodsNum(id.toString());
        List<GoodsSpecification> specifications = goodsSpecificationService.queryByGoodsNum(id.toString());
//        List<GoodsAttribute> attributes = attributeService.queryByGid(id);

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
        data.put("Stocks", Stocks);
//        data.put("attributes", attributes);
        data.put("categoryIds", categoryIds);

        return ResponseUtil.ok(data);
    }

    @GetMapping("/cat")
    public Object list2() {
        return adminGoodsService.list2();
    }

}
