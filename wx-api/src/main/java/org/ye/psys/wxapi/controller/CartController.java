package org.ye.psys.wxapi.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.core.util.JacksonUtil;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.entity.Address;
import org.ye.psys.db.entity.Cart;
import org.ye.psys.db.entity.Goods;
import org.ye.psys.db.entity.GoodsStock;
import org.ye.psys.db.service.AddressService;
import org.ye.psys.db.service.CartService;
import org.ye.psys.db.service.GoodsService;
import org.ye.psys.db.service.GoodsStockService;
import org.ye.psys.wxapi.annotation.LoginUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-04-03 08:55
 */
@RequestMapping("/wx/cart")
@RestController
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsStockService goodsStockService;
    @Autowired
    private AddressService addressService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object add(@LoginUser Integer userId,
                      @RequestBody Cart cart) {
        if (null == userId) {
            return ResponseUtil.unlogin();
        }
        Integer stockId = cart.getProductId();
        Integer number = cart.getNumber().intValue();
        String goodsn = cart.getGoodsSn();
        if (!ObjectUtils.allNotNull(stockId, number, goodsn)) {
            return ResponseUtil.badArgument();
        }
        //商品是否可购买
        Goods goods = goodsService.findByGoodsNum(goodsn);
        if (null == goods) {
            return ResponseUtil.fail("商品不存在或已下架");
        }
        GoodsStock stock = goodsStockService.findById(stockId);
        //判断购物车中是否存在此规格商品
        Cart exitCart = cartService.isExit(stockId, userId, goodsn);
        if (null == exitCart) {
            if (null == stock || number > stock.getCount()) {
                return ResponseUtil.fail("库存不足");
            }
            cart.setId(null);
            cart.setUserId(Integer.valueOf(goodsn));
            cart.setGoodsSn(goodsn);
            cart.setGoodsName(goods.getName());
            cart.setPicUrl(goods.getPicUrl());
            cart.setPrice(stock.getCurPrice());
            cart.setSpecifications(stock.getSpecifications());
            cart.setUserId(userId);
            cart.setChecked(true);
            cartService.add(cart);
        } else {
            int num = exitCart.getNumber() + number;
            if (num > stock.getCount()) {
                return ResponseUtil.fail("库存不足");
            }
            exitCart.setNumber(num);
            if (cartService.updateById(exitCart) == 0) {
                return ResponseUtil.updatedDataFailed();
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("userId", cartCount(userId));
        Integer id = cart.getId() == null ? exitCart.getId() : cart.getId();
        data.put("cartId", id);
        return data;
    }

    /**
     * 购物车商品货品种类数量
     * <p>
     * 如果用户没有登录，则返回空数据。
     *
     * @param userId 用户ID
     * @return 购物车商品货品种类数量
     */
    @RequestMapping(value = "/cartCount", method = RequestMethod.GET)
    public Object cartCount(@LoginUser Integer userId) {
        if (null == userId) {
            return ResponseUtil.unlogin();
        }
        List<Cart> cartList = cartService.queryByUid(userId);
        return ResponseUtil.ok(cartList.size());
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Object list(@LoginUser Integer userId) {
        if (null == userId) {
            return ResponseUtil.unlogin();
        }
        List<Cart> cartList = cartService.queryByUid(userId);
        Map<String, Object> cartInfo = new HashMap<>();


        //商品种类数
        int cartCount = cartList.size();
        //被选中商品种类数
        int checkedCartCount = 0;
        for (int i = 0; i < cartList.size(); ++i) {
            if (cartList.get(i).getChecked() == true) {
                checkedCartCount++;
            }
        }
        //被选中商品总价格
        double priceTotal = 0.00;
        for (int i = 0; i < cartList.size(); ++i) {
            Cart cart = cartList.get(i);
            if (cart.getChecked() == true) {
                priceTotal += cart.getNumber() * cart.getPrice().doubleValue();
            }
        }
        BigDecimal priceCount = new BigDecimal(priceTotal);
        Map<String, Object> data = new HashMap<>();
        data.put("cartList", cartList);
        cartInfo.put("priceCount", priceCount);
        cartInfo.put("checkedCartCount", checkedCartCount);
        cartInfo.put("cartCount", cartCount);
        data.put("cartInfo", cartInfo);
        return ResponseUtil.ok(data);
    }

    @RequestMapping(value = "/checked", method = RequestMethod.POST)
    public Object checked(@LoginUser Integer userId,
                          @RequestBody String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (body == null) {
            return ResponseUtil.badArgument();
        }
        Integer cartId = JacksonUtil.parseInteger(body, "cartId");
        Integer checkValue = JacksonUtil.parseInteger(body, "isChecked");
        if (checkValue == null) {
            return ResponseUtil.badArgument();
        }
        Boolean isChecked = (checkValue == 1);
        cartService.updateCheck(cartId, isChecked);

//        List<Integer> productIds = JacksonUtil.parseIntegerList(body, "productIds");
//        if (productIds == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        Integer checkValue = JacksonUtil.parseInteger(body, "isChecked");
//        if (checkValue == null) {
//            return ResponseUtil.badArgument();
//        }
//        Boolean isChecked = (checkValue == 1);
//
//        cartService.updateCheck(userId, productIds, isChecked);
        return list(userId);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Object delete(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (body == null) {
            return ResponseUtil.badArgument();
        }

        List<Integer> cartIds = JacksonUtil.parseIntegerList(body, "cartIds");

        if (cartIds == null || cartIds.size() == 0) {
            return ResponseUtil.badArgument();
        }

        cartService.delete(cartIds, userId);
        return list(userId);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Object update(@LoginUser Integer userId,
                         @RequestBody Cart cart) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (cart == null) {
            return ResponseUtil.badArgument();
        }
        Integer stockId = cart.getProductId();
        String goodsSn = cart.getGoodsSn();
        Integer number = cart.getNumber().intValue();
        Integer id = cart.getId();
        if (!ObjectUtils.allNotNull(id, number)) {
            return ResponseUtil.badArgument();
        }
        Cart existCart = cartService.isExit(stockId, userId, goodsSn);
        //判断商品是否可以购买
        Goods goods = goodsService.findByGoodsNum(goodsSn);
        if (goods == null || !goods.getIsOnSale()) {
            return ResponseUtil.fail("商品已下架");
        }

        //取得规格的信息,判断规格库存
        GoodsStock stock = goodsStockService.findById(stockId);
        if (stock == null || stock.getCount() < number) {
            return ResponseUtil.fail("库存不足");
        }

        existCart.setNumber(number);
        if (cartService.updateById(existCart) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 购物车下单
     *
     * @param body userId,count,productId
     * @return 购物车操作结果
     */
    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public Object checkout(@RequestBody String body) {
        Integer userId = JacksonUtil.parseInteger(body, "userId");
        if (null == userId) {
            return ResponseUtil.unlogin();
        }
        Integer count = JacksonUtil.parseInteger(body, "count");
        Integer cartId = JacksonUtil.parseInteger(body, "cartId");
        List<Cart> cartList = new ArrayList<>();
        BigDecimal total;
        //购物车购买
        if (cartId == 0) {
            //购买列表
            cartList = cartService.queryByUidAndChecked(userId);

            double totals = 0.00;
            for (int i = 0; i < cartList.size(); ++i) {
                Cart temp = cartList.get(i);
                totals = temp.getPrice().doubleValue() * temp.getNumber();
            }
            //总价格
            total = new BigDecimal(totals);
        } else {
            Integer productId = JacksonUtil.parseInteger(body, "productId");

            Cart cart = cartService.findByUserandPro(userId, productId);
            cart.setNumber(count);
            double totals = 0.00;
            totals = cart.getPrice().doubleValue() * count;
            //总价格
            total = new BigDecimal(totals);
            cartList.add(cart);
        }

        //收货地址
        Address address = addressService.findDefault(userId);
        if (null == address) {
            address = new Address();
            address.setId(0);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("address", address);
        data.put("cartList", cartList);
        data.put("total", total);
        return ResponseUtil.ok(data);

    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Object count(@LoginUser Integer userId) {
        if (null == userId) {
            return ResponseUtil.unlogin();
        }
        List<Cart> cartList = cartService.queryByUidAndChecked(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("cartCount", cartList.size());
        return ResponseUtil.ok(data);
    }

}

