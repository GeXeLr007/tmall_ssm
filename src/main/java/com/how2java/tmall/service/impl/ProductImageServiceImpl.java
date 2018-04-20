package com.how2java.tmall.service.impl;

import com.how2java.tmall.mapper.ProductImageMapper;
import com.how2java.tmall.mapper.ProductMapper;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProductImageServiceImpl implements ProductImageService {
    @Autowired
    ProductImageMapper productImageMapper;
    @Autowired
    ProductMapper productMapper;

    @Override
    public void add(ProductImage productImage) {
        productImageMapper.insert(productImage);
    }

    @Override
    public void delete(Integer id) {
        productImageMapper.deleteByPrimaryKey(id);
    }

    @Override
    public ProductImage get(Integer id) {
        return null;
    }

    @Override
    public void update(ProductImage productImage) {

    }

    @Override
    public List<ProductImage> list(Integer pid, String type) {
        return null;
    }
}
