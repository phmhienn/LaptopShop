package com.laptopstore.presentation.controller;

import com.laptopstore.application.dto.order.ShipmentDTO;
import com.laptopstore.application.facade.ShipmentFacade;
import com.laptopstore.common.constants.AppConstants;
import com.laptopstore.common.response.ApiResponse;
import com.laptopstore.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentFacade shipmentFacade;

    // Admin endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<ShipmentDTO>>> getAllShipments(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {

        PagedResponse<ShipmentDTO> response = shipmentFacade.getAllShipments(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentDTO>> getShipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(shipmentFacade.getShipmentById(id)));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentDTO>> getShipmentByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(shipmentFacade.getShipmentByOrderId(orderId)));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentDTO>> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Shipment status updated", shipmentFacade.updateShipmentStatus(id, status)));
    }

    @PutMapping("/{id}/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentDTO>> updateTrackingInfo(
            @PathVariable Long id,
            @RequestParam String trackingNumber,
            @RequestParam String shippingProvider) {
        return ResponseEntity.ok(ApiResponse.success("Tracking info updated", shipmentFacade.updateTrackingInfo(id, trackingNumber, shippingProvider)));
    }
}
