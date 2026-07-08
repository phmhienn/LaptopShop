package com.laptopstore.business.service;

import com.laptopstore.common.enums.ShipmentStatus;
import com.laptopstore.data.entity.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShipmentService {
    Shipment getShipmentById(Long id);
    Shipment getShipmentByOrderId(Long orderId);
    Page<Shipment> getAllShipments(Pageable pageable);
    Shipment updateShipmentStatus(Long id, ShipmentStatus status);
    Shipment updateTrackingNumber(Long id, String trackingNumber, String shippingProvider);
}
