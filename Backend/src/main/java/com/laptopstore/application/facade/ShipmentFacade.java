package com.laptopstore.application.facade;

import com.laptopstore.application.dto.order.ShipmentDTO;
import com.laptopstore.application.mapper.OrderMapper;
import com.laptopstore.business.service.ShipmentService;
import com.laptopstore.common.enums.ShipmentStatus;
import com.laptopstore.common.response.PagedResponse;
import com.laptopstore.data.entity.Shipment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShipmentFacade {

    private final ShipmentService shipmentService;
    private final OrderMapper orderMapper; // Reusing OrderMapper since toShipmentDTO is there

    public ShipmentDTO getShipmentById(Long id) {
        return orderMapper.toShipmentDTO(shipmentService.getShipmentById(id));
    }

    public ShipmentDTO getShipmentByOrderId(Long orderId) {
        return orderMapper.toShipmentDTO(shipmentService.getShipmentByOrderId(orderId));
    }

    public PagedResponse<ShipmentDTO> getAllShipments(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page - 1, size, sort);

        Page<Shipment> shipmentPage = shipmentService.getAllShipments(pageable);
        List<ShipmentDTO> content = shipmentPage.getContent().stream()
                .map(orderMapper::toShipmentDTO)
                .collect(Collectors.toList());

        return PagedResponse.of(
                content,
                shipmentPage.getNumber(),
                shipmentPage.getSize(),
                shipmentPage.getTotalElements(),
                shipmentPage.getTotalPages()
        );
    }

    public ShipmentDTO updateShipmentStatus(Long id, String statusStr) {
        ShipmentStatus status;
        try {
            status = ShipmentStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid shipment status");
        }
        return orderMapper.toShipmentDTO(shipmentService.updateShipmentStatus(id, status));
    }

    public ShipmentDTO updateTrackingInfo(Long id, String trackingNumber, String shippingProvider) {
        return orderMapper.toShipmentDTO(shipmentService.updateTrackingNumber(id, trackingNumber, shippingProvider));
    }
}
