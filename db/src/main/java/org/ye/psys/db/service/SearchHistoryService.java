package org.ye.psys.db.service;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ye.psys.db.entity.SearchHistory;
import org.ye.psys.db.entity.SearchHistoryExample;
import org.ye.psys.db.mapper.SearchHistoryMapper;
import org.ye.psys.db.entity.SearchHistory.Column;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-03-14 16:07
 */
@Service
public class SearchHistoryService {
    private Column[] columns = new Column[]{Column.keyword};
    @Autowired
    private SearchHistoryMapper searchHistoryMapper;

    public void save(SearchHistory searchHistory) {
        searchHistory.setCreateTime(LocalDateTime.now());
        searchHistory.setUpdateTime(LocalDateTime.now());
        searchHistoryMapper.insertSelective(searchHistory);
    }

    public List<SearchHistory> findByUserId(Integer userId) {
        SearchHistoryExample example = new SearchHistoryExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        example.setOrderByClause("update_time");
        return searchHistoryMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 删除指定历史搜索记录
     * @param userId
     * @param keyWord
     * @Return
     */
    public void deleteHistory(Integer userId, String keyWord) {
        SearchHistoryExample example = new SearchHistoryExample();
        example.or().andUserIdEqualTo(userId).andKeywordEqualTo(keyWord).andDeletedEqualTo(false);

        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setDeleted(true);

        searchHistoryMapper.updateByExampleSelective(searchHistory,example);
    }

    /**
     * 删除用户所有历史搜索记录
     * @param userId
     * @Return
     */
    public void deleteAllHistory(Integer userId) {
        SearchHistoryExample example = new SearchHistoryExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);

        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setDeleted(true);

        searchHistoryMapper.updateByExampleSelective(searchHistory,example);
    }

    public SearchHistory findByUserIdAndKey(Integer userId, String keyWord) {
        SearchHistoryExample example = new SearchHistoryExample();
        example.or().andUserIdEqualTo(userId).andKeywordEqualTo(keyWord).andDeletedEqualTo(false);
        return searchHistoryMapper.selectOneByExample(example);
    }

    public List<SearchHistory> querySelective(String userId, String keyword, Integer page, Integer size, String sort, String order) {
        SearchHistoryExample example = new SearchHistoryExample();
        SearchHistoryExample.Criteria criteria = example.createCriteria();

        if (!org.springframework.util.StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if (!org.springframework.util.StringUtils.isEmpty(keyword)) {
            criteria.andKeywordLike("%" + keyword + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!org.springframework.util.StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return searchHistoryMapper.selectByExample(example);
    }
}
