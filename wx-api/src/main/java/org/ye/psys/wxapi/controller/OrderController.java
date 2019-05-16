package org.ye.psys.wxapi.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ye.psys.core.util.JacksonUtil;
import org.ye.psys.db.entity.Cart;
import org.ye.psys.wxapi.annotation.LoginUser;
import org.ye.psys.wxapi.service.WxOrderService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-03-20 15:19
 */
@RestController
@RequestMapping("wx/order")
public class OrderController {
    private final Log logger = LogFactory.getLog(OrderController.class);

    @Autowired
    private WxOrderService wxOrderService;

    /**
     * 订单列表
     *
     * @param userId
     * @param type   0全部订单
     *               1待付款
     *               2待收货
     *               3待评价
     * @param page
     * @param size
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public Object list(@LoginUser Integer userId,
                       @RequestParam(defaultValue = "0") Integer type,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "1") Integer size) {
        return wxOrderService.list(userId, type, page, size);
    }

    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public Object detail(@LoginUser Integer userId,
                         @RequestParam("orderId") Integer orderId) {
        return wxOrderService.detail(userId, orderId);
    }

    /**
     * 确认收货
     *
     * @param body {userId,orderId }
     * @return 订单详情
     */
    @RequestMapping(value = "confirm", method = RequestMethod.POST)
    public Object confirm(@RequestBody String body) {
        Integer userId = JacksonUtil.parseInteger(body, "userId");
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        return wxOrderService.confirm(userId, orderId);
    }

    /**
     * 提交订单
     */
    @RequestMapping(value = "submit", method = RequestMethod.GET)
    public Object submit(@LoginUser Integer userId,
                         @RequestParam("addressId") Integer addressId,
                         @RequestParam("cartId") Integer cartId,
                         @RequestParam("count") Integer count
    ) {
        return wxOrderService.submit(userId, addressId, cartId, count);
    }

    /**
     * 准备支付
     * 目前支付只会失败
     */
    @RequestMapping(value = "prepay", method = RequestMethod.POST)
    public Object prepay(@LoginUser Integer userId,
                         @RequestBody String body,
                         HttpServletRequest httpServletRequest) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        return wxOrderService.perpay(userId, orderId, httpServletRequest);
    }

    /**
     * 取消订单
     */
    @RequestMapping(value = "cancel", method = RequestMethod.POST)
    public Object cancel(@RequestBody String body) {
        return wxOrderService.cancel(body);
    }

    /**
     * 支付成功
     */
    @RequestMapping(value = "pay", method = RequestMethod.POST)
    public Object paySucess(@LoginUser Integer userId,
                            @RequestBody String body,
                            HttpServletRequest httpServletRequest) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        return wxOrderService.paySucess(userId, orderId, httpServletRequest);
    }


}
