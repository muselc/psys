package org.ye.psys.wxapi.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ye.psys.core.util.ResponseUtil;
import org.ye.psys.db.entity.Category;
import org.ye.psys.db.service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("wx/category")
public class CategoryController {
    private final Log logger = LogFactory.getLog(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    /**
     * 分类主页
     *
     * @param id
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public Object index(Integer id) {

        //所有一级目录
        List<Category> primary = categoryService.findL1();

        //当前一级目录
        Category currentCategory = null;
        if (null == id) {
            currentCategory = categoryService.findById(primary.get(0).getId());
        } else {
            currentCategory = categoryService.findById(id);
        }

        //当前二级目录
        List<Category> second = categoryService.findByPid(currentCategory.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("primary", primary);
        data.put("currentCategory", currentCategory);
        data.put("second", second);

        return ResponseUtil.ok(data);
    }

    @RequestMapping(value = "/second", method = RequestMethod.GET)
    public Object second(@RequestParam("id") Integer id) {
        Category currentCategory = categoryService.findById(id);
        List<Category> second = categoryService.findByPid(currentCategory.getId());

        Map<String, Object> data = new HashMap<>();

        data.put("currentCategory", currentCategory);
        data.put("second", second);
        return ResponseUtil.ok(data);

    }
}
