package org.ye.psys.wxapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.ye.psys.core.util.JacksonUtil;
import org.ye.psys.core.util.OrderUtil;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.entity.*;
import org.ye.psys.db.service.*;
import org.ye.psys.wxapi.dao.CommentVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-04-11 15:42
 */
@RequestMapping("wx/comment")
@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderGoodsService orderGoodsService;
    @Autowired
    private UserService userService;
    @Autowired
    private GoodsService goodsService;

    /**
     * 添加评论
     * <p>
     * 1.查看订单是否存在
     * 2.检查订单状态
     * 3.添加评论并修改订单状态
     * </p>
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object add(@RequestBody String body) {
        Integer userId = JacksonUtil.parseInteger(body, "userId");
        if (null == userId) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        Orders order = ordersService.findById(orderId);
        if (order == null || orderId == 0) {
            return ResponseUtil.fail("无此订单");
        }
        if (!OrderUtil.isConfirmStatus(order) && !OrderUtil.isAutoConfirmStatus(order)) {
            return ResponseUtil.fail("此订单未确认收货，无法评价");
        }
        String score = JacksonUtil.parseString(body, "score");
        String content = JacksonUtil.parseString(body, "comment");
        if (!StringUtils.isEmpty(score) && !StringUtils.isEmpty(content)) {
            Comment comment = new Comment();
            comment.setOrderSn(orderId);
            comment.setContent(content);
            comment.setUserId(userId);
            comment.setIsreply(false);
            commentService.add(comment);
        }
        order.setOrderStatus(OrderUtil.STATUS_COMMENT);
        ordersService.update(order);
        return ResponseUtil.ok();
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Object list(@RequestParam String goodsSn) {
        List<Comment> commentList = new ArrayList<>();
        List<OrderGoods> orderGoodsList = orderGoodsService.findByGoodsSn(goodsSn);
        for (OrderGoods orderGoods : orderGoodsList) {
            Comment comment = commentService.findByOrderId(orderGoods.getOrderId());
            if (comment != null) {
                commentList.add(comment);
            }
        }

        List<CommentVo> commentVos = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentVo commentVo = new CommentVo();
            commentVo.setStar(comment.getStar());
            commentVo.setContent(comment.getContent());
            if (comment.getResponse()!=null) {
                commentVo.setResponse(comment.getResponse());
            }

            User user = userService.findById(comment.getUserId());
            commentVo.setName(user.getNickname());
            commentVo.setImg(user.getAvatar());
            Goods goods = goodsService.findByGoodsNum(goodsSn);
            commentVo.setGoodsName(goods.getName());
            commentVos.add(commentVo);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("commentList", commentVos);
        return ResponseUtil.ok(data);
    }

}
