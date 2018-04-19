package com.how2java.tmall.mapper;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.util.Page;

import java.util.List;

public interface CategoryMapper {
    List<Category> list(Page page);
    
    int total();
    
    int add(Category category);
    
    int delete(Integer id);

    Category get(Integer id);

    void update(Category category);
}
