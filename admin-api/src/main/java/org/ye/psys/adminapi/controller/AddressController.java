package org.ye.psys.adminapi.controller;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.adminapi.annotation.RequiresPermissionsDesc;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.core.validator.Order;
import org.ye.psys.core.validator.Sort;
import org.ye.psys.db.entity.Address;
import org.ye.psys.db.service.AddressService;
import org.ye.psys.db.service.AreaService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-04-19 13:50
 */
@RestController
@RequestMapping("/admin/address")
public class AddressController {
    private final Log logger = LogFactory.getLog(AddressController.class);

    @Autowired
    private AddressService addressService;

    @Autowired
    private AreaService areaService;

    @RequiresPermissions("admin:address:list")
    @RequiresPermissionsDesc(menu={"用户管理" , "收货地址"}, button="查询")
    @GetMapping("/list")
    public Object list(Integer userId, String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "create_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {

        List<Address> addressList = addressService.querySelective(userId, name, page, limit, sort, order);
        long total = PageInfo.of(addressList).getTotal();

        List<Map<String, Object>> addressVoList = new ArrayList<>(addressList.size());
        for (Address address : addressList) {
            Map<String, Object> addressVo = toVo(address);
            addressVoList.add(addressVo);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", addressVoList);

        return ResponseUtil.ok(data);
    }

    private Map<String, Object> toVo(Address address) {
        Map<String, Object> addressVo = new HashMap<>();
        addressVo.put("id", address.getId());
        addressVo.put("userId", address.getUserId());
        addressVo.put("name", address.getName());
        addressVo.put("mobile", address.getMobile());
        addressVo.put("isDefault", address.getIsDefault());
        addressVo.put("provinceId", address.getProvinceId());
        addressVo.put("cityId", address.getCityId());
        addressVo.put("areaId", address.getAreaId());
        addressVo.put("address", address.getDetail());
        String province = areaService.findById(address.getProvinceId()).getName();
        String city = areaService.findById(address.getCityId()).getName();
        String area = areaService.findById(address.getAreaId()).getName();
        addressVo.put("province", province);
        addressVo.put("city", city);
        addressVo.put("area", area);
        return addressVo;
    }
}
