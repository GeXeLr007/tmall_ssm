package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;

import java.util.List;

public interface OrderService {
    String waitPay = "waitPay";
    String waitDelivery = "waitDelivery";
    String waitConfirm = "waitConfirm";
    String waitReview = "waitReview";
    String finish = "finish";
    String delete = "delete";

    void add(Order order);

    void delete(Integer id);

    void update(Order order);

    Order get(Integer id);

    List<Order> list();

    float add(Order o,List<OrderItem> ois);

    List list(int uid, String excludedStatus);
}
