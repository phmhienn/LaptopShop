package com.laptopstore.business.service;

import com.laptopstore.application.dto.order.CheckoutRequestDTO;
import com.laptopstore.common.enums.ShipmentStatus;
import com.laptopstore.data.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    
    Order createOrder(Long userId, CheckoutRequestDTO checkoutRequest);
    
    Order getOrderByIdAndUser(Long orderId, Long userId);
    
    Order getOrderById(Long orderId);
    
    Order getOrderByCode(String orderCode);
    
    Page<Order> getOrdersByUser(Long userId, Pageable pageable);
    
    Page<Order> getAllOrders(String keyword, Pageable pageable);
    
    Page<Order> getOrdersByShipmentStatus(ShipmentStatus status, Pageable pageable);
    
    void cancelOrder(Long orderId, Long userId);
}
