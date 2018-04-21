package com.how2java.tmall.controller;

import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.PropertyValue;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("")
public class PropertyValueController {
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;
    
    @RequestMapping("admin_propertyValue_edit")
    public String edit(Integer pid, Model model){
//        根据pid获取product对象，用于面包屑导航的分类名字的显示
        Product product = productService.get(pid);
        model.addAttribute("p",product);
//        初始化该产品，使该产品具有属性值记录，但value属性都为null
        propertyValueService.init(product);
//        获取属性值list
        List<PropertyValue> propertyValues = propertyValueService.list(pid);
        model.addAttribute("pvs",propertyValues);
        
        return "admin/editPropertyValue";
    }

    @RequestMapping("admin_propertyValue_update")
    @ResponseBody
    public String update(PropertyValue propertyValue, Model model){
        propertyValueService.update(propertyValue);
//        与前端的期望返回结果保持一致，为success
        return "success";
    }
}
