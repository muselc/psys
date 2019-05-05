package org.ye.psys.db.service;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ye.psys.db.entity.Comment;
import org.ye.psys.db.entity.CommentExample;
import org.ye.psys.db.mapper.CommentMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-04-11 15:43
 */
@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    public void add(Comment comment) {
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.insertSelective(comment);
    }

    public List<Comment> querySelective(String userId, Integer orderId,Integer page, Integer limit, String sort, String order) {
        CommentExample example = new CommentExample();
        CommentExample.Criteria criteria = example.or();

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if (orderId != null && orderId != 0) {
            criteria.andOrderSnEqualTo(orderId);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return commentMapper.selectByExample(example);
    }

    public long count() {
        CommentExample example = new CommentExample();
        example.or().andDeletedEqualTo(false);
        return commentMapper.countByExample(example);
    }

    public Comment findById(Integer commentId) {
        return commentMapper.selectByPrimaryKey(commentId);
    }

    public void update(Comment comment) {
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateByPrimaryKey(comment);
    }

    public Comment findByOrderId(Integer orderId) {
        CommentExample example = new CommentExample();
        example.or().andDeletedEqualTo(false).andOrderSnEqualTo(orderId);
        return commentMapper.selectOneByExample(example);
    }
}
