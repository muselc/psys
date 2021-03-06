package org.ye.psys.wxapi.service;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.service.WxPayService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.core.config.Kd;
import org.ye.psys.core.config.KdApiOrderDistinguish;
import org.ye.psys.core.system.SystemConfig;
import org.ye.psys.core.util.JacksonUtil;
import org.ye.psys.core.util.OrderUtil;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.core.validator.Order;
import org.ye.psys.db.entity.*;
import org.ye.psys.db.service.*;
import org.ye.psys.wxapi.util.IpUtil;
import org.ye.psys.core.util.OrderHandleOption;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-03-20 15:57
 */
@Service
public class WxOrderService {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private GoodsStockService goodsStockService;

    @Autowired
    private CartService cartService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private UserService userService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private UserFormidService userFormidService;

    @Autowired
    private Kd kd;
    @Autowired
    private SystemService systemService;

    /**
     * 订单列表
     *
     * @param userId 用户ID
     * @param type   订单信息：
     *               0，全部订单；
     *               1，待付款；；
     *               2，待收货；
     *               3，待评价。
     * @param page   分页页数
     * @param size   分页大小
     * @return 订单列表
     */
    public Object list(Integer userId, Integer type, Integer page, Integer size) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        List<Short> orderStatus = OrderUtil.orderStatus(type);
        List<Orders> orderList = ordersService.findByStatus(userId, orderStatus);

        List<Map<String, Object>> oList = new ArrayList<>();
        for (Orders order : orderList) {
            Map<String, Object> omap = new HashMap<>();
            omap.put("id", order.getId());
            omap.put("orderSn", order.getOrderSn());
            omap.put("actualPrice", order.getActualPrice());
            omap.put("orderStatusText", OrderUtil.orderStatusText(order));
            omap.put("handleOption", OrderUtil.build(order));

            List<OrderGoods> orderGoodsList = orderGoodsService.findByOrderId(order.getId());

            List<Map<String, Object>> ogList = new ArrayList<>();
            for (OrderGoods orderGoods : orderGoodsList) {
                Map<String, Object> gmap = new HashMap<>();
                gmap.put("id", orderGoods.getId());
                gmap.put("id", orderGoods.getId());
                gmap.put("goodsName", orderGoods.getGoodsName());
                gmap.put("number", orderGoods.getNumber());
                gmap.put("picUrl", orderGoods.getPicUrl());
                ogList.add(gmap);
            }
            omap.put("goodsList", ogList);
            oList.add(omap);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", oList);

        return ResponseUtil.ok(result);
    }

    public Object submit(Integer userId, Integer addressId, Integer cartId, Integer count) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (addressId == null) {
            return ResponseUtil.badArgument();
        }
        List<Cart> cartList = new ArrayList<>();
        int countTemp = 0;
        if (cartId != 0) {
            Cart cart = cartService.findById(cartId);
            countTemp = cart.getNumber();
            cart.setNumber(count);
            cartList.add(cart);
        } else {
            cartList = cartService.queryByUidAndChecked(userId);
        }

        //库存是否足够
        for (int i = 0; i < cartList.size(); ++i) {
            GoodsStock stock = goodsStockService.findById(cartList.get(i).getProductId());
            if (null == stock || stock.getCount() < cartList.get(i).getNumber()) {
                return ResponseUtil.fail("库存不足！");
            }
        }
        //地址有效性
        Address address = addressService.findById(addressId);
        if (null == address) {
            return ResponseUtil.badArgument();
        }

        //运费
        BigDecimal freightPrice = new BigDecimal(0.00);

        double total = 0.00;
        for (int i = 0; i < cartList.size(); ++i) {
            Cart cart = cartList.get(i);
            total += cart.getNumber() * cart.getPrice().doubleValue();
        }
        //商品总价
        BigDecimal checkedGoodsPrice = new BigDecimal(total);

        if (checkedGoodsPrice.doubleValue() < systemService.findByKName(SystemConfig.MALL_EXPRESS_FREIGHT_MIN)){
            freightPrice = new BigDecimal(systemService.findByKName(SystemConfig.MALL_EXPRESS_FREIGHT_VALUE));
        }
        //订单总价
        BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice);
        //生成订单
        Orders order = new Orders();
        order.setUserId(userId);
        order.setOrderSn(ordersService.generateOrderSn());
        order.setOrderStatus(OrderUtil.STATUS_CREATE);
        order.setConsignee(address.getName());
        order.setMobile(address.getMobile());
        String detailedAddress = detailedAddress(address);
        order.setAddress(detailedAddress);
        order.setGoodsPrice(checkedGoodsPrice);

        order.setFreightPrice(freightPrice);
        order.setActualPrice(orderTotalPrice);

        ordersService.add(order);
        int orderId = order.getId();

        //生成订单商品表
        for (Cart cart : cartList) {
            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setOrderId(orderId);
            orderGoods.setGoodsId(cart.getGoodsId());
            orderGoods.setGoodsSn(cart.getGoodsSn());
            orderGoods.setProductId(cart.getProductId());
            orderGoods.setGoodsName(cart.getGoodsName());
            orderGoods.setPicUrl(cart.getPicUrl());
            orderGoods.setPrice(cart.getPrice());
            orderGoods.setNumber(cart.getNumber());
            orderGoods.setSpecifications(cart.getSpecifications());

            orderGoodsService.add(orderGoods);
        }

        //删除购物车已下单的
        if (cartId != 0) {
            Cart cart = cartList.get(0);
            countTemp = countTemp - cart.getNumber();
            if (countTemp > 0) {
                cart.setNumber(countTemp);
                cartService.update(cart);
            } else if (countTemp == 0) {
                cartService.deleteById(cart.getId());
            } else if (countTemp < 0) {
                return ResponseUtil.fail();
            }
        } else {
            for (int i = 0; i < cartList.size(); ++i) {
                cartService.deleteById(cartList.get(i).getId());
            }
        }
        //库存减少
        for (Cart cart : cartList) {
            Integer stockId = cart.getProductId();
            GoodsStock goodsStock = goodsStockService.findById(stockId);
            int left = 0;
            if (cartId != 0) {
                left = goodsStock.getCount() - count;
            } else {
                left = goodsStock.getCount() - cart.getNumber();
            }
            if (left < 0) {
                throw new RuntimeException("下单的商品货品数量大于库存量");
            }
            goodsStock.setCount(left);
            goodsStockService.reduceNum(stockId, goodsStock.getCount() - left);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        return ResponseUtil.ok(data);
    }

    private String detailedAddress(Address address) {
        Integer provinceId = address.getProvinceId();
        Integer cityId = address.getCityId();
        Integer areaId = address.getAreaId();
        String provinceName = areaService.findById(provinceId).getName();
        String cityName = areaService.findById(cityId).getName();
        String areaName = areaService.findById(areaId).getName();
        String fullRegion = provinceName + " " + cityName + " " + areaName;
        return fullRegion + " " + address.getDetail();
    }

    public Object perpay(Integer userId, Integer orderId, HttpServletRequest httpServletRequest) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }
        Orders order = ordersService.findById(orderId);
        if (null == order) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }
        //订单是否可支付
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isPay()) {
            return ResponseUtil.fail("状态码不对，订单不能支付");
        }
        User user = userService.findById(userId);
        String openId = user.getWeixinOpenid();
        if (openId == null) {
            return ResponseUtil.fail("openId缺失，订单不能支付");
        }
        WxPayMpOrderResult result = null;
        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setOutTradeNo(order.getOrderSn());
            orderRequest.setOpenid(openId);
            orderRequest.setBody("订单：" + order.getOrderSn());

            orderRequest.setTotalFee(order.getActualPrice().intValue());
            orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(httpServletRequest));
            result = wxPayService.createOrder(orderRequest);
            //缓存prepay
            String prepayId = result.getPackageValue();
            prepayId = prepayId.replace("prepay_id=", "");

            UserFormid userFormid = new UserFormid();
            userFormid.setOpenid(user.getWeixinOpenid());
            userFormid.setFormid(prepayId);
            userFormid.setIsprepay(true);
            userFormid.setUseamount(3);
            userFormid.setExpireTime(LocalDateTime.now().plusDays(7));
            userFormidService.add(userFormid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.fail("订单不能支付");
        }
        //更新时间

        return ResponseUtil.ok(result);
    }

    public Object detail(Integer userId, Integer orderId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Orders order = ordersService.findById(orderId);
        if (null == order) {
            return ResponseUtil.fail("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.fail("不是当前用户的订单");
        }

        Map<String, Object> orderVo = new HashMap<String, Object>();
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderSn());
        orderVo.put("addTime", order.getCreateTime());
        orderVo.put("consignee", order.getConsignee());
        orderVo.put("mobile", order.getMobile());
        orderVo.put("address", order.getAddress());
        orderVo.put("goodsPrice", order.getGoodsPrice());
        orderVo.put("freightPrice", order.getFreightPrice());
        orderVo.put("actualPrice", order.getActualPrice());
        orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
        orderVo.put("handleOption", OrderUtil.build(order));
        orderVo.put("expCode", order.getShipChannel());
        orderVo.put("expNo", order.getShipSn());

        KdApiOrderDistinguish api = new KdApiOrderDistinguish(kd);
        try {
            String expNo = order.getShipSn();
            JSONObject dataJson = new JSONObject(api.getOrderTracesByJson(expNo));
            String expCode = dataJson.getJSONArray("Shippers").getJSONObject(0).getString("ShipperCode");

            String result = api.getOrderTracesByJson(expCode, expNo);
            JSONObject data = new JSONObject(result);
            JSONArray traces = data.getJSONArray("Traces");
            orderVo.put("Traces", traces.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<OrderGoods> orderGoodsList = orderGoodsService.findByOrderId(order.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderGoods", orderGoodsList);

        return ResponseUtil.ok(result);
    }

    public Object confirm(Integer userId, Integer orderId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        Orders order = ordersService.findById(orderId);
        if (null == order) {
            return ResponseUtil.fail("订单不存在");
        }
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isConfirm()) {
            return ResponseUtil.fail("订单不能确认收货");
        }

        //修改订单状态
        order.setOrderStatus(OrderUtil.STATUS_CONFIRM);
        order.setConfirmTime(LocalDateTime.now());
        if (ordersService.update(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }

        //修改订单商品状态
        List<OrderGoods> orderGoodsList = orderGoodsService.findByOrderId(orderId);
        for (OrderGoods orderGoods : orderGoodsList) {
            orderGoods.setIsfinish(true);
            orderGoodsService.update(orderGoods);
        }
        return ResponseUtil.ok();
    }

    /**
     * 取消订单
     * 1.检测订单状态是否可取消
     * 2.库存恢复
     * 3.设置订单为取消状态
     *
     * @param body
     */
    public Object cancel(String body) {
        Integer userId = JacksonUtil.parseInteger(body, "userId");
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        Orders order = ordersService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        //检测订单状态是否可取消
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isCancel()) {
            return ResponseUtil.fail("订单状态码错误，无法取消");
        }

        //库存恢复
        List<OrderGoods> orderGoodsList = orderGoodsService.findByOrderId(orderId);
        for (OrderGoods orderGoods : orderGoodsList) {
            Integer stockId = orderGoods.getProductId();
            int number = orderGoods.getNumber();
            if (goodsStockService.addStock(stockId, number) == 0) {
                throw new RuntimeException("商品货品库存增加失败");
            }
        }

        //设置订单为取消状态
        order.setOrderStatus(OrderUtil.STATUS_AUTO_CANCEL);
        order.setEndTime(LocalDateTime.now());
        if (ordersService.update(order) == 0) {
            throw new RuntimeException("更新数据已失效");
        }
        return ResponseUtil.ok();
    }

    /**
     * 模拟支付成功
     */
    public Object paySucess(Integer userId, Integer orderId, HttpServletRequest httpServletRequest) {

        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }
        Orders order = ordersService.findById(orderId);
        if (null == order) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }
        //订单是否可支付
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isPay()) {
            return ResponseUtil.fail("状态码不对，订单不能支付");
        }
        User user = userService.findById(userId);
        String openId = user.getWeixinOpenid();
        if (openId == null) {
            return ResponseUtil.fail("openId缺失，订单不能支付");
        }
        order.setOrderStatus(OrderUtil.STATUS_PAY);
        order.setPayTime(LocalDateTime.now());
        ordersService.update(order);
        return ResponseUtil.ok();

    }

    public Object refund(Integer orderId) {
        Orders order = ordersService.findById(orderId);
        if (OrderUtil.isPayStatus(order)) {
            order.setOrderStatus(OrderUtil.STATUS_REFUND);
        }
        ordersService.update(order);
        return ResponseUtil.ok();
    }
}
