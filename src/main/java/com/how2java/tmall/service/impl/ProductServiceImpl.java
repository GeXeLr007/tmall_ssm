package com.how2java.tmall.service.impl;

import com.how2java.tmall.mapper.ProductMapper;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductExample;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;

    @Override
    public void add(Product product) {
        productMapper.insert(product);
    }

    @Override
    public Product get(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
//        返回product对象之前，为其设置这两个属性
        setFirstProductImage(product);
        setCategory(product);
        return product;
    }

    @Override
    public void delete(Integer id) {
        productMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Product product) {
        productMapper.updateByPrimaryKeySelective(product);
    }

    @Override
    public List<Product> list(Integer cid) {
        Category category = categoryService.get(cid);
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andCidEqualTo(cid);
        productExample.setOrderByClause("id desc");
        List<Product> products = productMapper.selectByExample(productExample);
        //        返回product对象之前，为其设置这两个属性
        setCategory(products);
        setFirstProductImage(products);
        return products;
    }

    @Override
    public void setFirstProductImage(Product product) {
        List<ProductImage> productImages = productImageService.list(product.getId(), ProductImageService.type_single);
        if (!productImages.isEmpty()){
            ProductImage productImage = productImages.get(0);
            product.setFirstProductImage(productImage);
        }
    }

    @Override
    public void setFirstProductImage(List<Product> products) {
        for (Product product:
             products) {
            setFirstProductImage(product);
        }
    }

    public void setCategory(List<Product> products) {
        for (Product product : products) {
            setCategory(product);
        }
    }

    public void setCategory(Product product) {
        Category category = categoryService.get(product.getCid());
        product.setCategory(category);
    }
}
