package com.how2java.tmall.service;

import com.how2java.tmall.pojo.ProductImage;

import java.util.List;

public interface ProductImageService {
    String type_single = "type_single";
    String type_detail = "type_detail";

    void add(ProductImage productImage);

    void delete(Integer id);

    ProductImage get(Integer id);

    void update(ProductImage productImage);

    List<ProductImage> list(Integer pid, String type);

}
