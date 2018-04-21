package com.how2java.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("")
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;

    @RequestMapping("admin_product_add")
    public String add(Product product) {
        product.setCreateDate(new Date());
        productService.add(product);
        return "redirect:admin_product_list?cid=" + product.getCid();
    }

    @RequestMapping("admin_product_edit")
    public String edit(Integer id, Model model) {
        Product product = productService.get(id);
        model.addAttribute("p", product);
        return "admin/editProduct";
    }

    @RequestMapping("admin_product_update")
    public String update(Product product) {
        productService.update(product);
        return "redirect:admin_product_list?cid=" + product.getCid();
    }

    @RequestMapping("admin_product_delete")
    public String update(Integer id) {
        Product product = productService.get(id);
        productService.delete(id);
        return "redirect:admin_product_list?cid=" + product.getCid();
    }

    @RequestMapping("admin_product_list")
    public String list(Integer cid, Model model, Page page) {
        PageHelper.offsetPage(page.getStart(), page.getCount());
        List<Product> products = productService.list(cid);
        int total = (int) new PageInfo<>(products).getTotal();
        page.setTotal(total);
        page.setParam("&cid=" + cid);
        
//        为每一个product对象设置firstProductImage属性
        for (Product product:products
             ) {
            productService.setFirstProductImage(product);
        }
        model.addAttribute("ps", products);
        Category category = categoryService.get(cid);
        model.addAttribute("c", category);
        model.addAttribute("page", page);
        return "admin/listProduct";
    }


}
