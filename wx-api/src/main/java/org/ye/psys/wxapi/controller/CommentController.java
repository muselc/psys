package org.ye.psys.wxapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.db.service.CommentService;

/**
 * @Author liansongye
 * @create 2019-04-11 15:42
 */
@RequestMapping("wx/comment")
@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

}
