package org.ye.psys.wxapi.controller;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.db.entity.User;
import org.ye.psys.db.service.UserService;
import org.ye.psys.wxapi.dao.LoginInfo;
import org.ye.psys.wxapi.dao.UserInfo;

import javax.servlet.http.HttpServletRequest;

import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.core.util.UserToken;
import org.ye.psys.core.util.UserTokenManager;
import org.ye.psys.wxapi.util.IpUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("wx/auth")
@Validated
public class WxAuthController {
    private final Log logger = LogFactory.getLog(WxAuthController.class);

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private UserService userService;

    /**
     * 微信登录
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Object login(@RequestBody LoginInfo loginInfo, HttpServletRequest request) {
        String code = loginInfo.getCode();
        UserInfo userInfo = loginInfo.getUserInfo();
        if (code == null || userInfo == null) {
            return ResponseUtil.badArgument();
        }

        String sessionKey = null;
        String openId = null;
        try {
            WxMaJscode2SessionResult result = this.wxMaService.getUserService().getSessionInfo(code);
            sessionKey = result.getSessionKey();
            openId = result.getOpenid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sessionKey == null || openId == null) {
            return ResponseUtil.fail();
        }
        User user = userService.findByWxId(openId);
        if (user == null) {
            user = new User();
            user.setWeixinOpenid(openId);
            user.setAvatar(userInfo.getAvatarUrl());
            user.setNickname(userInfo.getNickName());
            user.setGender(userInfo.getGender());
            user.setUserLevel((byte) 0);
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IpUtil.client(request));

            userService.add(user);
        } else {
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IpUtil.client(request));
            if (userService.updateById(user) == 0) {
                return ResponseUtil.updatedDataFailed();
            }
        }

        UserToken userToken = UserTokenManager.generateToken(user.getId());
        userToken.setSessionKey(sessionKey);

        Map<Object, Object> result = new HashMap<>();
        result.put("token", userToken);
        result.put("tokrnExpire", userToken.getExpireTime().toString());
        result.put("userInfo", userInfo);
        return ResponseUtil.ok(result);
    }

}
