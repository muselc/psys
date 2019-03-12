package org.ye.psys.db.service;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.Goods;
import org.ye.psys.db.entity.Goods.Column;
import org.ye.psys.db.entity.GoodsExample;
import org.ye.psys.db.mapper.GoodsMapper;

import java.util.List;

@Service
public class GoodsService {
    Column[] columns = new Column[]{Column.id, Column.name, Column.detail, Column.picUrl, Column.isHot,Column.isNew,Column.originalPrice,Column.currentPrice};

    @Autowired
    private GoodsMapper goodsMapper;

    public List<Goods> findByHot(int offset, int limit){
        GoodsExample example = new GoodsExample();
        example.or().andIsHotEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("create_time desc");
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExampleSelective(example,columns);
    }

    public List<Goods> findByNew(int offset ,int limit){
        GoodsExample example = new GoodsExample();
        example.or().andIsNewEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("create_time desc");
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExampleSelective(example,columns);
    }


}
