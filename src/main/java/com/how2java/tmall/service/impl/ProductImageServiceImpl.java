package com.how2java.tmall.service.impl;

import com.how2java.tmall.mapper.ProductImageMapper;
import com.how2java.tmall.mapper.ProductMapper;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.pojo.ProductImageExample;
import com.how2java.tmall.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageServiceImpl implements ProductImageService {
    @Autowired
    ProductImageMapper productImageMapper;

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
        return productImageMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(ProductImage productImage) {
        productImageMapper.updateByPrimaryKeySelective(productImage);
    }

    @Override
    public List<ProductImage> list(Integer pid, String type) {
        ProductImageExample productImageExample = new ProductImageExample();
        productImageExample.createCriteria().andTypeEqualTo(type).andPidEqualTo(pid);
        productImageExample.setOrderByClause("id desc");
        return productImageMapper.selectByExample(productImageExample);
    }
}
