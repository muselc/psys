package org.ye.psys.adminapi.service;

import com.github.binarywang.wxpay.service.WxPayService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.ye.psys.core.config.Kd;
import org.ye.psys.core.config.KdApiOrderDistinguish;
import org.ye.psys.core.util.JacksonUtil;
import org.ye.psys.core.util.OrderUtil;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.entity.OrderGoods;
import org.ye.psys.db.entity.Orders;
import org.ye.psys.db.entity.User;
import org.ye.psys.db.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import  static org.ye.psys.adminapi.util.AdminResponseCode.*;

@Service
public class AdminOrderService {
    private final Log logger = LogFactory.getLog(AdminOrderService.class);

    @Autowired
    private OrderGoodsService orderGoodsService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private GoodsStockService goodsStockService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private Kd kd;

    @Transactional
    public Object list(Integer userId, String orderSn, List<Short> orderStatusArray,
                       Integer page, Integer limit, String sort, String order) {
        List<Orders> orderList = ordersService.querySelective(userId, orderSn, orderStatusArray, page, limit, sort, order);
        long total = PageInfo.of(orderList).getTotal();

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", orderList);

        return ResponseUtil.ok(data);
    }

    @Transactional
    public Object detail(Integer id) {
        Orders order = ordersService.findById(id);
        List<OrderGoods> orderGoods = orderGoodsService.findByOrderId(id);
        User user = userService.findById(order.getUserId());
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderGoods", orderGoods);
        data.put("user", user);

        return ResponseUtil.ok(data);
    }

    /**
     * 订单退款
     * <p>
     * 1. 检测当前订单是否能够退款;
     * 2. 微信退款操作;
     * 3. 设置订单退款确认状态；
     * 4. 订单商品库存回库。
     * <p>
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @Transactional
    public Object refund(String body) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String refundMoney = JacksonUtil.parseString(body, "refundMoney");
        if (orderId == null) {
           return ResponseUtil.badArgument();
        }
        if (StringUtils.isEmpty(refundMoney)) {
            return ResponseUtil.badArgument();
        }

        Orders order = ordersService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }

        if (order.getActualPrice().compareTo(new BigDecimal(refundMoney)) != 0) {
            return ResponseUtil.badArgumentValue();
        }

        // 如果订单不是退款状态，则不能退款
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_REFUND)) {
            return ResponseUtil.fail(ORDER_CONFIRM_NOT_ALLOWED, "订单不能退款");
        }
         //设置订单取消状态
        order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
        if (ordersService.update(order) == 0) {
            throw new RuntimeException("更新数据已失效");
        }

        // 商品货品数量增加
        List<OrderGoods> orderGoodsList = orderGoodsService.findByOrderId(orderId);
        for (OrderGoods orderGoods : orderGoodsList) {
            Integer stockId = orderGoods.getProductId();
            int number = orderGoods.getNumber();
            if (goodsStockService.addStock(stockId, number) == 0) {
                throw new RuntimeException("商品货品库存增加失败");
            }
        }
        return ResponseUtil.ok();
    }
//    @Transactional
//    public Object refund(String body) {
//        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
//        String refundMoney = JacksonUtil.parseString(body, "refundMoney");
//        if (orderId == null) {
//            return ResponseUtil.badArgument();
//        }
//        if (StringUtils.isEmpty(refundMoney)) {
//            return ResponseUtil.badArgument();
//        }
//
//        Orders order = ordersService.findById(orderId);
//        if (order == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        if (order.getActualPrice().compareTo(new BigDecimal(refundMoney)) != 0) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        // 如果订单不是退款状态，则不能退款
//        if (!order.getOrderStatus().equals(OrderUtil.STATUS_REFUND)) {
//            return ResponseUtil.fail(ORDER_CONFIRM_NOT_ALLOWED, "订单不能确认收货");
//        }
//
//        // 微信退款
//        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
//        wxPayRefundRequest.setOutTradeNo(order.getOrderSn());
//        wxPayRefundRequest.setOutRefundNo("refund_" + order.getOrderSn());
//        // 元转成分
//        Integer totalFee = order.getActualPrice().multiply(new BigDecimal(100)).intValue();
//        wxPayRefundRequest.setTotalFee(totalFee);
//        wxPayRefundRequest.setRefundFee(totalFee);
//
//        WxPayRefundResult wxPayRefundResult = null;
//        try {
//            wxPayRefundResult = wxPayService.refund(wxPayRefundRequest);
//        } catch (WxPayException e) {
//            e.printStackTrace();
//            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
//        }
//        if (!wxPayRefundResult.getReturnCode().equals("SUCCESS")) {
//            logger.warn("refund fail: " + wxPayRefundResult.getReturnMsg());
//            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
//        }
//        if (!wxPayRefundResult.getResultCode().equals("SUCCESS")) {
//            logger.warn("refund fail: " + wxPayRefundResult.getReturnMsg());
//            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
//        }
//
//        // 设置订单取消状态
//        order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
//        if (ordersService.update(order) == 0) {
//            throw new RuntimeException("更新数据已失效");
//        }
//
//        // 商品货品数量增加
//        List<OrderGoods> orderGoodsList = orderGoodsService.findByOrderId(orderId);
//        for (OrderGoods orderGoods : orderGoodsList) {
//            Integer stockId = orderGoods.getProductId();
//            int number = orderGoods.getNumber();
//            if (goodsStockService.addStock(stockId, number) == 0) {
//                throw new RuntimeException("商品货品库存增加失败");
//            }
//        }
//
//        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND, new String[]{order.getOrderSn().substring(8, 14)});
//
//        return ResponseUtil.ok();
//    }

    /**
     * 发货
     * 1. 检测当前订单是否能够发货
     * 2. 设置订单发货状态
     *
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     */
    @Transactional
    public Object ship(String body) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String shipSn = JacksonUtil.parseString(body, "shipSn");
        String shipChannel = JacksonUtil.parseString(body, "shipChannel");
        if (orderId == null || shipSn == null || shipChannel == null) {
            return ResponseUtil.badArgument();
        }

        Orders order = ordersService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }

        // 如果订单不是已付款状态，则不能发货
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_PAY)) {
            return ResponseUtil.fail(ORDER_CONFIRM_NOT_ALLOWED, "订单不能确认收货");
        }

        order.setOrderStatus(OrderUtil.STATUS_SHIP);
        order.setShipSn(shipSn);
        order.setShipChannel(shipChannel);
        order.setShipTime(LocalDateTime.now());
        if (ordersService.update(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }

        return ResponseUtil.ok();
    }

    public Object kdInfo(String body) {
        String expNo = JacksonUtil.parseString(body, "expNo");
        KdApiOrderDistinguish api = new KdApiOrderDistinguish(kd);
        try {
            JSONObject dataJson = new JSONObject(api.getOrderTracesByJson(expNo));
            String expCode = dataJson.getJSONArray("Shippers").getJSONObject(0).getString("ShipperCode");

            String result = api.getOrderTracesByJson(expCode, expNo);
            JSONObject data = new JSONObject(result);
            Map<String, Object> map = data.toMap();
            return ResponseUtil.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseUtil.fail();
    }


}
