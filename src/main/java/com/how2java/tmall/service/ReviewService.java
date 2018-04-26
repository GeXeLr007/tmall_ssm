package com.how2java.tmall.service;

import com.how2java.tmall.mapper.ReviewMapper;
import com.how2java.tmall.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ReviewService {
    void add(Review c);

    void delete(int id);

    void update(Review c);

    Review get(int id);

    List<Review> list(int pid);

    int getCount(int pid);

}
