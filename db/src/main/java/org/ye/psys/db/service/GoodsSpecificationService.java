package org.ye.psys.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.GoodsSpecification;
import org.ye.psys.db.entity.GoodsSpecificationExample;
import org.ye.psys.db.mapper.GoodsSpecificationMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-03-27 15:28
 */
@Service
public class GoodsSpecificationService {
    @Autowired
    private GoodsSpecificationMapper goodsSpecificationMapper;


    public Object getSpecificationList(String id) {
        List<GoodsSpecification> goodsSpecificationList = findByGoodsNum(id);
        Map<String, GoodsSpecificationDto> map = new HashMap<>();
        List<GoodsSpecificationDto> dtoList = new ArrayList<>();

        for (GoodsSpecification goodsSpecification : goodsSpecificationList) {
            String specification = goodsSpecification.getSpecification();
           GoodsSpecificationDto dto = map.get(specification);
            if (null==dto) {
               dto = new GoodsSpecificationDto();
               dto.setName(specification);
               List<GoodsSpecification> valueList = new ArrayList<>();
               valueList.add(goodsSpecification);
               dto.setValueList(valueList);
               map.put(specification,dto);
               dtoList.add(dto);
            } else {
                List<GoodsSpecification> valueList = dto.getValueList();
                valueList.add(goodsSpecification);
            }
        }
        return dtoList;
    }

    private List<GoodsSpecification> findByGoodsNum(String id) {
        GoodsSpecificationExample example = new GoodsSpecificationExample();
        example.or().andGoodsNumEqualTo(id);
        return goodsSpecificationMapper.selectByExample(example);
    }

    public void deleteByGid(Integer gid) {
        GoodsSpecificationExample example = new GoodsSpecificationExample();
        example.or().andGoodsNumEqualTo(gid.toString());
        goodsSpecificationMapper.logicalDeleteByExample(example);
    }

    public List<GoodsSpecification> queryByGoodsNum(String id) {
        GoodsSpecificationExample example = new GoodsSpecificationExample();
        example.or().andGoodsNumEqualTo(id).andDeletedEqualTo(false);
        return goodsSpecificationMapper.selectByExample(example);
    }

    public void add(GoodsSpecification specification) {
        goodsSpecificationMapper.insertSelective(specification);
    }

    private class GoodsSpecificationDto {
        private String name;
        private List<GoodsSpecification> valueList;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<GoodsSpecification> getValueList() {
            return valueList;
        }

        public void setValueList(List<GoodsSpecification> valueList) {
            this.valueList = valueList;
        }
    }
}
