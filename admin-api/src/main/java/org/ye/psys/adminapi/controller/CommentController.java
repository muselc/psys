package org.ye.psys.adminapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.ye.psys.core.util.JacksonUtil;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.core.validator.Order;
import org.ye.psys.core.validator.Sort;
import org.ye.psys.db.entity.Comment;
import org.ye.psys.db.service.CommentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author liansongye
 * @create 2019-04-30 13:58
 */
@RestController
@RequestMapping("admin/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/list")
    public Object list(String userId, Integer orderId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "create_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<Comment> commentList = commentService.querySelective(userId, orderId,page, limit, sort, order);
        long total = commentService.count();
        Map<String, Object> data = new HashMap<>();
        data.put("commentList", commentList);
        data.put("total", total);
        return ResponseUtil.ok(data);
    }

    @PostMapping("/reply")
    public Object reply(@RequestBody String body) {
        Integer commentId = JacksonUtil.parseInteger(body, "commentId");
        if (commentId == null || commentId == 0) {
            return ResponseUtil.badArgument();
        }
        Comment comment = commentService.findById(commentId);
        // 目前只支持回复一次
        if (comment.getIsreply()) {
            return ResponseUtil.fail( "订单商品已回复！");
        }
        String content = JacksonUtil.parseString(body, "content");

        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.badArgument();
        }
       comment.setResponse(content);
        comment.setIsreply(true);
          // 评价回复没有用
        commentService.update(comment);
        return ResponseUtil.ok();
    }
}
