package com.laptopstore.business.service.impl;

import com.laptopstore.business.exception.ResourceNotFoundException;
import com.laptopstore.business.service.ShipmentService;
import com.laptopstore.common.enums.ShipmentStatus;
import com.laptopstore.data.entity.Shipment;
import com.laptopstore.data.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;

    @Override
    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));
    }

    @Override
    public Shipment getShipmentByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "orderId", orderId));
    }

    @Override
    public Page<Shipment> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Shipment updateShipmentStatus(Long id, ShipmentStatus status) {
        Shipment shipment = getShipmentById(id);
        shipment.updateStatus(status);
        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public Shipment updateTrackingNumber(Long id, String trackingNumber, String shippingProvider) {
        Shipment shipment = getShipmentById(id);
        shipment.setTrackingNumber(trackingNumber);
        shipment.setShippingProvider(shippingProvider);
        return shipmentRepository.save(shipment);
    }
}
