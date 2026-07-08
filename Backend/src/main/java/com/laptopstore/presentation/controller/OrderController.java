package com.laptopstore.presentation.controller;

import com.laptopstore.application.dto.order.CheckoutRequestDTO;
import com.laptopstore.application.dto.order.OrderDTO;
import com.laptopstore.application.facade.OrderFacade;
import com.laptopstore.common.constants.AppConstants;
import com.laptopstore.common.response.ApiResponse;
import com.laptopstore.common.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    // User Endpoints
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderDTO>> checkout(@Valid @RequestBody CheckoutRequestDTO request) {
        OrderDTO order = orderFacade.checkout(request);
        return ResponseEntity.ok(ApiResponse.success("Order placed successfully", order));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PagedResponse<OrderDTO>>> getMyOrders(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        PagedResponse<OrderDTO> response = orderFacade.getMyOrders(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderDTO>> getMyOrderDetails(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderFacade.getMyOrderDetails(id)));
    }

    @PostMapping("/me/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> cancelMyOrder(@PathVariable Long id) {
        orderFacade.cancelMyOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully"));
    }

    // Admin Endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<OrderDTO>>> searchOrders(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        PagedResponse<OrderDTO> response = orderFacade.searchOrders(keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<OrderDTO>>> getOrdersByStatus(
            @PathVariable String status,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        PagedResponse<OrderDTO> response = orderFacade.getOrdersByStatus(status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderFacade.getOrderById(id)));
    }

}
