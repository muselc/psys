package org.ye.psys.adminapi.service;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.ye.psys.adminapi.dao.GoodsAllinone;
import org.ye.psys.adminapi.util.CatVo;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.entity.Category;
import org.ye.psys.db.entity.Goods;
import org.ye.psys.db.entity.GoodsSpecification;
import org.ye.psys.db.entity.GoodsStock;
import org.ye.psys.db.service.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AdminGoodsService {
    private final Log logger = LogFactory.getLog(AdminGoodsService.class);

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsSpecificationService specificationService;

    @Autowired
    private GoodsStockService productService;
    @Autowired
    private CategoryService categoryService;

    

    public Object list(String goodsNum, String name,String catagory,
                       Integer page, Integer limit, String sort, String order) {
        List<Goods> goodsList = goodsService.querySelective(goodsNum, name,catagory, page, limit, sort, order);
        long total = PageInfo.of(goodsList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", goodsList);

        return ResponseUtil.ok(data);
    }

    private Object validate(GoodsAllinone goodsAllinone) {
        Goods goods = goodsAllinone.getGoods();
        String name = goods.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }
        String goodsSn = goods.getGoodsNum();
        if (StringUtils.isEmpty(goodsSn)) {
            return ResponseUtil.badArgument();
        }
        
        // 分类可以不设置，如果设置则需要验证分类存在
        Integer categoryId = goods.getCategoryId();
        if (categoryId != null && categoryId != 0) {
            if (categoryService.findById(categoryId) == null) {
                return ResponseUtil.badArgumentValue();
            }
        }

        GoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        for (GoodsSpecification specification : specifications) {
            String spec = specification.getSpecification();
            if (StringUtils.isEmpty(spec)) {
                return ResponseUtil.badArgument();
            }
            String value = specification.getValue();
            if (StringUtils.isEmpty(value)) {
                return ResponseUtil.badArgument();
            }
        }

        GoodsStock[] products = goodsAllinone.getProducts();
        for (GoodsStock product : products) {
            Integer number = product.getCount();
            if (number == null || number < 0) {
                return ResponseUtil.badArgument();
            }

            BigDecimal price = product.getCurPrice();
            if (price == null) {
                return ResponseUtil.badArgument();
            }

            String[] productSpecifications = product.getSpecifications();
            if (productSpecifications.length == 0) {
                return ResponseUtil.badArgument();
            }
        }

        return null;
    }

    /**
     * 编辑商品
     * <p>
     * TODO
     * 目前商品修改的逻辑是
     * 1. 更新_goods表
     * 2. 逻辑删除_goods_specification、_goods_product
     * 3. 添加_goods_specification、_goods_product
     * <p>
     * 这里商品三个表的数据采用删除再添加的策略是因为
     * 商品编辑页面，支持管理员添加删除商品规格、添加删除商品属性，因此这里仅仅更新是不可能的，
     * 只能删除三个表旧的数据，然后添加新的数据。
     * 但是这里又会引入新的问题，就是存在订单商品货品ID指向了失效的商品货品表。
     * 因此这里会拒绝管理员编辑商品，如果订单或购物车中存在商品。
     * 所以这里可能需要重新设计。
     */
    @Transactional
    public Object update(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        Goods goods = goodsAllinone.getGoods();
        GoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        GoodsStock[] products = goodsAllinone.getProducts();

        Integer id = goods.getId();

        // 商品基本信息表_goods
        if (goodsService.updateById(goods) == 0) {
            throw new RuntimeException("更新数据失败");
        }

        Integer gid = goods.getId();
        specificationService.deleteByGid(gid);
        productService.deleteByGid(gid);

        // 商品规格表_goods_specification
        for (GoodsSpecification specification : specifications) {
            specification.setGoodsNum(goods.getId().toString());
            specificationService.add(specification);
        }

        // 商品货品表_stock
        for (GoodsStock product : products) {
            product.setGoodsNum(goods.getId().toString());
            productService.add(product);
        }

        return ResponseUtil.ok();
    }

    @Transactional
    public Object delete(Goods goods) {
        Integer id = goods.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        Integer gid = goods.getId();
        goodsService.deleteById(gid);
        specificationService.deleteByGid(gid);
        productService.deleteByGid(gid);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        Goods goods = goodsAllinone.getGoods();
        GoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        GoodsStock[] products = goodsAllinone.getProducts();

        String goodsNum = goods.getGoodsNum();
        if (goodsService.checkExistByGoodsNum(goodsNum)){
            return ResponseUtil.fail( "商品编号重复");
        }
        String name = goods.getName();
        if (goodsService.checkExistByName(name)) {
            return ResponseUtil.fail( "商品名已经存在");
        }

        goods.setId(Integer.valueOf(goodsNum));
        // 商品基本信息表_goods
        goodsService.add(goods);

        // 商品规格表_goods_specification
        for (GoodsSpecification specification : specifications) {
            specification.setGoodsNum(goods.getId().toString());
            specificationService.add(specification);
        }

        // 商品货品表_product
        for (GoodsStock product : products) {
            product.setGoodsNum(goods.getId().toString());
            productService.add(product);
        }
        return ResponseUtil.ok();
    }

    public Object list2() {
        List<Category> l1CatList = categoryService.findL1();
        List<CatVo> categoryList = new ArrayList<>(l1CatList.size());

        for (Category l1 : l1CatList) {
            CatVo l1CatVo = new CatVo();
            l1CatVo.setValue(l1.getId());
            l1CatVo.setLabel(l1.getName());

            List<Category> l2CatList = categoryService.findByPid(l1.getId());
            List<CatVo> children = new ArrayList<>(l2CatList.size());
            for (Category l2 : l2CatList) {
                CatVo l2CatVo = new CatVo();
                l2CatVo.setValue(l2.getId());
                l2CatVo.setLabel(l2.getName());
                children.add(l2CatVo);
            }
            l1CatVo.setChildren(children);

            categoryList.add(l1CatVo);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("categoryList", categoryList);
        return ResponseUtil.ok(data);
    }

    public Object detail(Integer id) {
        Goods goods = goodsService.findByGoodsNum(id.toString());
        List<GoodsStock> products = productService.findByGoodsNum(id.toString());
        List<GoodsSpecification> specifications = specificationService.queryByGoodsNum(id.toString());

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
        data.put("products", products);
        data.put("categoryIds", categoryIds);

        return ResponseUtil.ok(data);
    }

}
