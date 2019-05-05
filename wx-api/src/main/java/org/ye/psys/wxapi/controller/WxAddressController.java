package org.ye.psys.wxapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.entity.Address;
import org.ye.psys.db.entity.Area;
import org.ye.psys.db.service.AddressService;
import org.ye.psys.db.service.AreaService;
import org.ye.psys.wxapi.annotation.LoginUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liansongye
 * @create 2019-03-22 14:24
 */
@RequestMapping("/wx/address")
@RestController
public class WxAddressController {
    @Autowired
    private AddressService addressService;
    @Autowired
    private AreaService areaService;


    /**
     * 获取地址
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Object getAddress(@LoginUser Integer userId) {
        List<Address> addresses = addressService.getAddress(userId);
        List<Map<String, Object>> addressList = new ArrayList<>();
        for (Address address : addresses) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("id", address.getId());
            temp.put("name", address.getName());
            temp.put("moible", address.getMobile());
            String pname = areaService.findById(address.getProvinceId()).getName();
            String cname = areaService.findById(address.getCityId()).getName();
            String aname = areaService.findById(address.getAreaId()).getName();
            String area = pname + " " + cname + " " + aname;
            temp.put("area", area);
            temp.put("detail", address.getDetail());
            temp.put("isDefault", address.getIsDefault());
            addressList.add(temp);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("addressList", addressList);
        return ResponseUtil.ok(data);
    }

    /**
     * 删除地址
     *
     * @param address 地址id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Object deleteAddress(@RequestBody Address address) {
        if (address.getId() == null) {
            return ResponseUtil.badArgument();
        }
        addressService.deleteAddress(address.getId());
        return ResponseUtil.ok();
    }

    /**
     * 创建或修改地址
     *
     * @param address
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Object updateAddress(@LoginUser Integer userId, @RequestBody Address address) {
        if (null == address.getId() || 0 == address.getId()) {
            address.setId(null);
            address.setUserId(userId);
            addressService.addAddress(address);
            Area area = areaService.findById(address.getProvinceId());
            area.setValue(area.getValue() + 1);
            areaService.updateValue(area);
        } else {
            address.setUserId(userId);
            Address addressTemp = addressService.findById(address.getId());
            if (null != address.getProvinceId() && !addressTemp.getProvinceId().equals(address.getProvinceId())) {
                Area aread = areaService.findById(addressTemp.getProvinceId());
                aread.setValue(aread.getValue() - 1);
                areaService.updateValue(aread);

                Area areau = areaService.findById(address.getProvinceId());
                areau.setValue(areau.getValue() + 1);
                areaService.updateValue(areau);
            }
            if (null != address.getName())
                addressTemp.setName(address.getName());
            if (null != address.getMobile())
                addressTemp.setMobile(address.getMobile());
            if (null != address.getProvinceId())
                addressTemp.setProvinceId(address.getProvinceId());
            if (null != address.getCityId())
                addressTemp.setCityId(address.getCityId());
            if (null != address.getAreaId())
                addressTemp.setAreaId(address.getAreaId());
            if (null != address.getDetail())
                addressTemp.setDetail(address.getDetail());
            if (null != address.getIsDefault() && address.getIsDefault().equals(!addressTemp.getIsDefault()))
                addressTemp.setIsDefault(address.getIsDefault());
            addressService.updateAddress(addressTemp);
        }
        return ResponseUtil.ok(address.getId());
    }

    /**
     * 地址详情
     *
     * @param id 地址id
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public Object detailAddress(@RequestParam(value = "id") Integer id) {
        Address address = addressService.findById(id);
        if (null == address) {
            return ResponseUtil.badArgumentValue();
        }
        Map<Object, Object> data = new HashMap<>();
        data.put("id", address.getId());
        data.put("name", address.getName());
        data.put("provinceId", address.getProvinceId());
        data.put("cityId", address.getCityId());
        data.put("areaId", address.getAreaId());
        data.put("mobile", address.getMobile());
        data.put("detail", address.getDetail());
        data.put("isDefault", address.getIsDefault());
        String pname = areaService.findById(address.getProvinceId()).getName() + " ";
        data.put("provinceName", pname);
        String cname = areaService.findById(address.getCityId()).getName() + " ";
        data.put("cityName", cname);
        String aname = areaService.findById(address.getAreaId()).getName();
        data.put("areaName", aname);

        return ResponseUtil.ok(data);
    }

}
