package org.ye.psys.adminapi.controller;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.ye.psys.adminapi.annotation.RequiresPermissionsDesc;
import org.ye.psys.core.util.RegexUtil;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.core.util.bcrypt.BCryptPasswordEncoder;
import org.ye.psys.core.validator.Order;
import org.ye.psys.core.validator.Sort;
import org.ye.psys.db.entity.User;
import org.ye.psys.db.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ye.psys.adminapi.util.AdminResponseCode.*;

/**
 * @Author liansongye
 * @create 2019-04-16 09:54
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {
    private final Log logger = LogFactory.getLog(UserController.class);


    @Autowired
    private UserService userService;

    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String username, String mobile,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "create_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<User> userList = userService.querySelective(username, mobile, page, limit, sort, order);
        long total = PageInfo.of(userList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", userList);

        return ResponseUtil.ok(data);
    }
    @RequiresPermissions("admin:user:create")
    @RequiresPermissionsDesc(menu={"用户管理" , "会员管理"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody User user) {
        Object error = validate(user);
        if (error != null) {
            return error;
        }
        String username = user.getUsername();
        String mobile = user.getMobile();
        List<User> userList = userService.queryByUsername(username);
        if (userList.size() > 0) {
            return ResponseUtil.fail(USER_NAME_EXIST, "用户名已注册");
        }
        userList = userService.queryByMobile(mobile);
        if (userList.size() > 0) {
            return ResponseUtil.fail(USER_MOBILE_EXIST, "手机号已注册");
        }
        if (!RegexUtil.isMobileExact(mobile)) {
            return ResponseUtil.fail(USER_INVALID_MOBILE, "手机号格式不正确");
        }

        String password = user.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);

        userService.add(user);
        return ResponseUtil.ok(user);
    }

    @RequiresPermissions("admin:user:update")
    @RequiresPermissionsDesc(menu={"用户管理" , "会员管理"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody User user) {
        Object error = validate(user);
        if (error != null) {
            return error;
        }
        // 用户密码加密存储
        String password = user.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);

        if (userService.updateById(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(user);
    }

    @RequiresPermissions("admin:user:ageandsex")
    @RequiresPermissionsDesc(menu={"参谋" , "数据分析"}, button="年龄和性别")
    @GetMapping("/ageandsex")
    public Object ageAndSexData() {
        int noMan = userService.findBySex(0);
        int man = userService.findBySex(1);
        int woMan = userService.findBySex(2);

        Map<String, Integer> sexMap = new HashMap<>(3);

        sexMap.put("未知", noMan);
        sexMap.put("男", man);
        sexMap.put("女", woMan);
        //性别数据
        List sexList = new ArrayList();
        for (String key : sexMap.keySet()) {
            Map temp = new HashMap();
            temp.put("name", key);
            temp.put("value", sexMap.get(key));
            sexList.add(temp);
        }

        //年龄数据
        List ageList = new ArrayList();

        Map<Integer, Integer> time = new HashMap<>();
        time.put(0, 15);
        time.put(16, 25);
        time.put(26, 35);
        time.put(36, 45);
        time.put(46, 200);
        for (Integer start : time.keySet()) {
            Map temp = new HashMap();
            Integer end = time.get(start);
            temp.put("name", start + "-" + end);
            temp.put("value", userService.countByAge(start, end));
            ageList.add(temp);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("sex", sexList);
        data.put("age", ageList);
        return ResponseUtil.ok(data);
    }

    private Object validate(User user) {
        String username = user.getUsername();
        if (StringUtils.isEmpty(user)) {
            return ResponseUtil.badArgument();
        }
        if (!RegexUtil.isUsername(username)) {
            return ResponseUtil.fail(USER_INVALID_NAME, "用户名不符合规定");
        }
        String password = user.getPassword();
        if (StringUtils.isEmpty(password) || password.length() < 6) {
            return ResponseUtil.fail(USER_INVALID_PASSWORD, "用户密码长度不能小于6");
        }
        String mobile = user.getMobile();
        if (StringUtils.isEmpty(mobile)) {
            return ResponseUtil.badArgument();
        }
        if (!RegexUtil.isMobileExact(mobile)) {
            return ResponseUtil.fail(USER_INVALID_MOBILE, "用户手机号码格式不正确");
        }
        return null;
    }

}
