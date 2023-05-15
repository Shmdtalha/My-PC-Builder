package com.projects.mypcb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.Order;
import com.projects.mypcb.entity.OrderedComponent;
import com.projects.mypcb.global.OrderStatus;
import com.projects.mypcb.repository.OrderRepository;
import com.projects.mypcb.repository.OrderedComponentRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderedComponentRepository orderedComponentRepository;

    public Order saveOrder(Order order) {
        List<OrderedComponent> orderedComponents = new ArrayList<>();

        for (OrderedComponent orderedComponent : order.getOrderedComponents()) {
            orderedComponent.setOrder(order);
            orderedComponents.add(orderedComponentRepository.save(orderedComponent));
        }

        order.setOrderedComponents(orderedComponents);
        return orderRepository.save(order);
    }


    public List<Order> getAllOrders(OrderStatus filter) {
        List<Order> list ;
        if (filter == null || filter == OrderStatus.all) {
            list = orderRepository.findAll();
            Collections.reverse(list);
            return list;
        } else {
            list = orderRepository.findByOrderStatus(filter);
            Collections.reverse(list);
            return list;
        }

    }

    public Optional<Order> getOrderById(long id) {
        return orderRepository.findById(id);
    }
    public List<OrderedComponent> getAllOrderedComponents(OrderStatus filter) {
        List<Order> orders;
        
        if (filter == null) {
            orders = getAllOrders(null);
        } else {
            orders = getAllOrders(filter);
        }

        List<OrderedComponent> allOrderedComponents = new ArrayList<>();

        for (Order order : orders) {
            allOrderedComponents.addAll(order.getOrderedComponents());
        }

        return allOrderedComponents;
    }

}
