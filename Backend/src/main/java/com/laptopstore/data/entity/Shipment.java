package com.laptopstore.data.entity;

import com.laptopstore.common.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "receiver_address", nullable = false, columnDefinition = "TEXT")
    private String receiverAddress;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "shipping_provider", length = 100)
    private String shippingProvider;

    @Column(name = "shipping_fee", precision = 12, scale = 2)
    @Builder.Default
    private java.math.BigDecimal shippingFee = java.math.BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // DDD Methods
    public void ship() {
        this.status = ShipmentStatus.SHIPPING;
        this.shippedAt = LocalDateTime.now();
    }

    public void deliver() {
        this.status = ShipmentStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = ShipmentStatus.FAILED;
    }

    public void returnShipment() {
        this.status = ShipmentStatus.RETURNED;
    }

    public void updateStatus(ShipmentStatus newStatus) {
        if (newStatus == ShipmentStatus.SHIPPING) {
            ship();
        } else if (newStatus == ShipmentStatus.DELIVERED) {
            deliver();
        } else if (newStatus == ShipmentStatus.FAILED) {
            fail();
        } else if (newStatus == ShipmentStatus.RETURNED) {
            returnShipment();
        } else {
            this.status = newStatus;
        }
    }
}
