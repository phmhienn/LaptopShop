package com.laptopstore.business.service.impl;

import com.laptopstore.application.dto.order.CheckoutRequestDTO;
import com.laptopstore.business.exception.BusinessException;
import com.laptopstore.business.exception.ResourceNotFoundException;
import com.laptopstore.business.exception.ValidationException;
import com.laptopstore.business.service.CartService;
import com.laptopstore.business.service.OrderService;
import com.laptopstore.business.service.ProductService;
import com.laptopstore.business.service.UserService;
import com.laptopstore.common.constants.AppConstants;
import com.laptopstore.common.enums.PaymentStatus;
import com.laptopstore.common.enums.ShipmentStatus;
import com.laptopstore.data.entity.*;
import com.laptopstore.data.repository.CouponRepository;
import com.laptopstore.data.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;
    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public Order createOrder(Long userId, CheckoutRequestDTO request) {
        Cart cart = cartService.getCartByUserId(userId);
        
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessException("Cannot create order from an empty cart");
        }

        User user = userService.getUserById(userId);
        Order order = new Order();
        order.setUser(user);
        order.setOrderCode(AppConstants.ORDER_CODE_PREFIX + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        order.setNote(request.getNote());

        // Initialize Shipment
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setReceiverName(request.getShippingName());
        shipment.setReceiverPhone(request.getShippingPhone());
        shipment.setReceiverAddress(request.getShippingAddress());
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setShippingFee(java.math.BigDecimal.ZERO); // Default or could be calculated
        
        order.setShipment(shipment);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            
            // Check inventory again
            if (product.getStock() < cartItem.getQuantity()) {
                throw new ValidationException("Not enough stock for product: " + product.getName());
            }
            
            // Deduct inventory
            productService.updateInventory(product.getId(), product.getStock() - cartItem.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getEffectivePrice());
            orderItem.setQuantity(cartItem.getQuantity());
            
            BigDecimal subtotal = product.getEffectivePrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setSubtotal(subtotal);
            
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);
        
        // Handle Coupon logic
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", request.getCouponCode()));
                    
            if (!coupon.isValid()) {
                throw new ValidationException("Coupon is invalid or expired");
            }
            
            discountAmount = coupon.calculateDiscount(totalAmount);
            order.setCoupon(coupon);
            order.setDiscountAmount(discountAmount);
            
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        order.setFinalAmount(totalAmount.subtract(discountAmount));

        // Create Payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : AppConstants.DEFAULT_PAYMENT_METHOD);
        payment.setAmount(order.getFinalAmount());
        payment.setStatus(PaymentStatus.UNPAID);
        order.setPayment(payment);



        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful order creation
        cartService.clearCart(userId);

        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderByIdAndUser(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderByCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderCode", orderCode));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return orderRepository.findAll(pageable);
        }
        return orderRepository.searchOrders(keyword.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByShipmentStatus(ShipmentStatus status, Pageable pageable) {
        return orderRepository.findByShipmentStatus(status, pageable);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = getOrderByIdAndUser(orderId, userId);
        if (order.getShipment().getStatus() != ShipmentStatus.PENDING && order.getShipment().getStatus() != ShipmentStatus.READY_TO_SHIP) {
            throw new ValidationException("Cannot cancel order that is already being processed or shipped");
        }
        
        order.getShipment().setStatus(ShipmentStatus.FAILED);
        if (order.getPayment().getStatus() == PaymentStatus.UNPAID) {
            order.getPayment().setStatus(PaymentStatus.FAILED);
        }

        // Restore inventory
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            productService.updateInventory(product.getId(), product.getStock() + item.getQuantity());
        }

        orderRepository.save(order);
    }
}
