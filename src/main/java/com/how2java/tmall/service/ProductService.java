package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Product;

import java.util.List;

public interface ProductService {

    void add(Product product);

    Product get(Integer id);

    void delete(Integer id);

    void update(Product product);

    List<Product> list(Integer cid);
}
