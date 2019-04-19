package org.ye.psys.wxapi.service;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.service.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.entity.*;
import org.ye.psys.db.service.*;
import org.ye.psys.wxapi.util.IpUtil;
import org.ye.psys.wxapi.util.OrderHandleOption;
import org.ye.psys.wxapi.util.OrderUtil;

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
    private CommentService commentService;

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

    public Object submit(Integer userId, Integer addressId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (addressId == null) {
            return ResponseUtil.badArgument();
        }
        List<Cart> cartList = cartService.queryByUidAndChecked(userId);
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
        order.setOrderPrice(orderTotalPrice);
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
        for (int i = 0; i < cartList.size(); ++i) {
            cartService.deleteById(cartList.get(i).getId());
        }
        //库存减少
        for (Cart cart : cartList) {
            Integer stockId = cart.getProductId();
            GoodsStock goodsStock = goodsStockService.findById(stockId);
            int left = goodsStock.getCount() - cart.getNumber();
            if (left < 0) {
                throw new RuntimeException("下单的商品货品数量大于库存量");
            }
            goodsStock.setCount(left);
            goodsStockService.reduceNum(stockId, left);
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
        if (null==order){
            return ResponseUtil.fail( "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.fail("不是当前用户的订单");
        }

        Map<String, Object> orderVo = new HashMap<String, Object>();
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderSn());
        orderVo.put("addTime", order.getAddTime());
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
        if (null==order){
            return ResponseUtil.fail( "订单不存在");
        }
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isConfirm()) {
            return ResponseUtil.fail( "订单不能确认收货");
        }

        //修改订单状态
        order.setOrderStatus(OrderUtil.STATUS_CONFIRM);
        order.setConfirmTime(LocalDateTime.now());
        if (ordersService.update(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }
        return ResponseUtil.ok();
    }
}
