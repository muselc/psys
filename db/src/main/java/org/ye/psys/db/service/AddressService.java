package org.ye.psys.db.service;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ye.psys.db.entity.Address;
import org.ye.psys.db.entity.AddressExample;
import org.ye.psys.db.mapper.AddressMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author liansongye
 * @create 2019-03-22 14:26
 */
@Service
public class AddressService {
    @Autowired
    private AddressMapper addressMapper;

    public List<Address> getAddress(Integer userId) {
        AddressExample example = new AddressExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return addressMapper.selectByExample(example);
    }

    public void deleteAddress(Integer id) {
        AddressExample example = new AddressExample();
        example.or().andIdEqualTo(id);
        addressMapper.deleteByExample(example);
    }

    public int addAddress(Address address) {
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        return addressMapper.insertSelective(address);
    }

    public int updateAddress(Address address) {
        address.setUpdateTime(LocalDateTime.now());
        //看是否有默认地址
        if (address.getIsDefault()==true) {
            AddressExample example = new AddressExample();
            example.or().andIsDefaultEqualTo(true);
            List<Address> address1 = addressMapper.selectByExample(example);
            if (address1.size()>0&&address.getId().equals(address1.get(0).getId())&&address.getIsDefault().equals(address1.get(0).getIsDefault())){
                address1.get(0).setIsDefault(false);
                addressMapper.updateByPrimaryKey(address1.get(0));
            }

        }
        return addressMapper.updateByPrimaryKey(address);
    }

    public Address findById(Integer id) {
        return addressMapper.selectByPrimaryKey(id);
    }

    public Address findDefault(Integer userId) {
        AddressExample example = new AddressExample();
        example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true);
        return addressMapper.selectOneByExample(example);
    }

    public List<Address> querySelective(Integer userId, String name, Integer page, Integer limit, String sort, String order) {
        AddressExample example = new AddressExample();
        AddressExample.Criteria criteria = example.createCriteria();

        if (userId != null) {
            criteria.andUserIdEqualTo(userId);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return addressMapper.selectByExample(example);
    }

}
