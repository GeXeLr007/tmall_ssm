package com.how2java.tmall.service.impl;

import com.how2java.tmall.mapper.OrderItemMapper;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.OrderItemService;
import com.how2java.tmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    ProductService productService;

    @Override
    public void add(OrderItem orderItem) {
        orderItemMapper.insert(orderItem);
    }

    @Override
    public void delete(int id) {
        orderItemMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(OrderItem orderItem) {
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
    }

    @Override
    public OrderItem get(int id) {
        return orderItemMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<OrderItem> list() {
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.setOrderByClause("id desc");
        return orderItemMapper.selectByExample(orderItemExample);
    }

    @Override
    public void fill(List<Order> orders) {
        for (Order order :
                orders) {
            fill(order);
        }
    }

    @Override
    public void fill(Order order) {
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOidEqualTo(order.getId());
        List<OrderItem> orderItems = orderItemMapper.selectByExample(orderItemExample);
//        为每个orderItem设置product对象属性
        setProduct(orderItems);

        float total = 0;
        int totalNumber = 0;
        for (OrderItem orderItem :
                orderItems) {
            total += orderItem.getNumber() * orderItem.getProduct().getPromotePrice();
            totalNumber += orderItem.getNumber();
        }

        order.setTotal(total);
        order.setTotalNumber(totalNumber);
        order.setOrderItems(orderItems);

    }

    private void setProduct(List<OrderItem> orderItems) {
        for (OrderItem orderItem :
                orderItems) {
            setProduct(orderItem);
        }
    }

    private void setProduct(OrderItem orderItem) {
        Product product = productService.get(orderItem.getPid());
        orderItem.setProduct(product);
    }
}
