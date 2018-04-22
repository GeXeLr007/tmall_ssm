package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;

import java.util.List;

public interface ProductService {

    void add(Product product);

    Product get(Integer id);

    void delete(Integer id);

    void update(Product product);

    List<Product> list(Integer cid);

    void setFirstProductImage(Product product);
    
    void setFirstProductImage(List<Product> products);
    
    void fill(Category category);
    
    void fill(List<Category> categories);
    
    void fillByRow(List<Category> categories);
}
