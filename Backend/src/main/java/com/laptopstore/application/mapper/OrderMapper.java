package com.laptopstore.application.mapper;

import com.laptopstore.application.dto.order.OrderDTO;
import com.laptopstore.application.dto.order.OrderItemDTO;
import com.laptopstore.application.dto.order.PaymentDTO;
import com.laptopstore.application.dto.order.ShipmentDTO;
import com.laptopstore.data.entity.Order;
import com.laptopstore.data.entity.OrderItem;
import com.laptopstore.data.entity.Payment;
import com.laptopstore.data.entity.Shipment;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderDTO toOrderDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalAmount(order.getFinalAmount());
        
        dto.setNote(order.getNote());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getOrderItems() != null) {
            dto.setItems(order.getOrderItems().stream()
                    .map(this::toOrderItemDTO)
                    .collect(Collectors.toList()));
        }

        if (order.getPayment() != null) {
            dto.setPayment(toPaymentDTO(order.getPayment()));
        }

        if (order.getShipment() != null) {
            dto.setShipment(toShipmentDTO(order.getShipment()));
        }

        return dto;
    }

    public OrderItemDTO toOrderItemDTO(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductName(item.getProductName());
        dto.setProductPrice(item.getProductPrice());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());

        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProductThumbnail(item.getProduct().getThumbnail());
        }

        return dto;
    }

    public PaymentDTO toPaymentDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setMethod(payment.getMethod());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus().name());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaidAt(payment.getPaidAt());

        return dto;
    }

    public ShipmentDTO toShipmentDTO(Shipment shipment) {
        if (shipment == null) {
            return null;
        }

        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(shipment.getId());
        if (shipment.getOrder() != null) {
            dto.setOrderId(shipment.getOrder().getId());
        }
        dto.setReceiverName(shipment.getReceiverName());
        dto.setReceiverPhone(shipment.getReceiverPhone());
        dto.setReceiverAddress(shipment.getReceiverAddress());
        dto.setTrackingNumber(shipment.getTrackingNumber());
        dto.setShippingProvider(shipment.getShippingProvider());
        dto.setShippingFee(shipment.getShippingFee());
        dto.setStatus(shipment.getStatus().name());
        dto.setShippedAt(shipment.getShippedAt());
        dto.setDeliveredAt(shipment.getDeliveredAt());

        return dto;
    }
}
