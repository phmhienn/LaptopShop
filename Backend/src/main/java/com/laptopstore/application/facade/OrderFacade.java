package com.laptopstore.application.facade;

import com.laptopstore.application.dto.order.CheckoutRequestDTO;
import com.laptopstore.application.dto.order.OrderDTO;
import com.laptopstore.application.mapper.OrderMapper;
import com.laptopstore.application.security.services.UserDetailsImpl;
import com.laptopstore.business.service.OrderService;
import com.laptopstore.common.enums.ShipmentStatus;
import com.laptopstore.common.response.PagedResponse;
import com.laptopstore.data.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderFacade {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @Transactional
    public OrderDTO checkout(CheckoutRequestDTO request) {
        Long userId = getCurrentUserId();
        return orderMapper.toOrderDTO(orderService.createOrder(userId, request));
    }

    public OrderDTO getMyOrderDetails(Long orderId) {
        Long userId = getCurrentUserId();
        return orderMapper.toOrderDTO(orderService.getOrderByIdAndUser(orderId, userId));
    }

    @Transactional
    public void cancelMyOrder(Long orderId) {
        Long userId = getCurrentUserId();
        orderService.cancelOrder(orderId, userId);
    }

    public PagedResponse<OrderDTO> getMyOrders(int page, int size, String sortBy, String sortDir) {
        Long userId = getCurrentUserId();
        Pageable pageable = getPageable(page, size, sortBy, sortDir);
        return mapToPagedResponse(orderService.getOrdersByUser(userId, pageable));
    }

    // Admin methods

    public OrderDTO getOrderById(Long orderId) {
        return orderMapper.toOrderDTO(orderService.getOrderById(orderId));
    }

    public OrderDTO getOrderByCode(String orderCode) {
        return orderMapper.toOrderDTO(orderService.getOrderByCode(orderCode));
    }

    public PagedResponse<OrderDTO> searchOrders(String keyword, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = getPageable(page, size, sortBy, sortDir);
        return mapToPagedResponse(orderService.getAllOrders(keyword, pageable));
    }

    public PagedResponse<OrderDTO> getOrdersByStatus(String status, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = getPageable(page, size, sortBy, sortDir);
        ShipmentStatus shipmentStatus = ShipmentStatus.valueOf(status.toUpperCase());
        return mapToPagedResponse(orderService.getOrdersByShipmentStatus(shipmentStatus, pageable));
    }

    // Helper methods
    
    private Pageable getPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private PagedResponse<OrderDTO> mapToPagedResponse(Page<Order> ordersPage) {
        List<OrderDTO> content = ordersPage.getContent().stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());

        return PagedResponse.of(content, ordersPage.getNumber(), ordersPage.getSize(),
                ordersPage.getTotalElements(), ordersPage.getTotalPages());
    }
}
