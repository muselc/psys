package org.ye.psys.db.service;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ye.psys.db.entity.Issue;
import org.ye.psys.db.entity.IssueExample;
import org.ye.psys.db.mapper.IssueMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-04-19 14:54
 */
@Service
public class IssueService {
    @Autowired
    private IssueMapper issueMapper;

    public List<Issue> query() {
        IssueExample example = new IssueExample();
        example.or().andDeletedEqualTo(false);
        return issueMapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        issueMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(Issue issue) {
        issue.setCreateTime(LocalDateTime.now());
        issue.setUpdateTime(LocalDateTime.now());
        issueMapper.insertSelective(issue);
    }

    public List<Issue> querySelective(String question, Integer page, Integer size, String sort, String order) {
        IssueExample example = new IssueExample();
        IssueExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(question)) {
            criteria.andQuestionLike("%" + question + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return issueMapper.selectByExample(example);
    }

    public int updateById(Issue issue) {
        issue.setUpdateTime(LocalDateTime.now());
        return issueMapper.updateByPrimaryKeySelective(issue);
    }

    public Issue findById(Integer id) {
        return issueMapper.selectByPrimaryKey(id);
    }
}
