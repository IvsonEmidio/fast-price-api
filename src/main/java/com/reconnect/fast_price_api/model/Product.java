package com.reconnect.fast_price_api.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue
    private UUID uuid;
    
    @Column(nullable = false)
    private String id;
    
    @Column(nullable = false)
    private String link;
    
    @Column(nullable = true)
    private Long price;
    
    @Column(name = "sku_id", nullable = false)
    private String skuId;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public UUID getUuid() {
        return uuid;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Id cannot be null or empty");
        }
        this.id = id;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public Long getPrice() {
        return price;
    }
    
    public void setPrice(Long price) {
        this.price = price;
    }
    
    public String getSkuId() {
        return skuId;
    }
    
    public void setSkuId(String skuId) {
        if (skuId == null || skuId.trim().isEmpty()) {
            throw new IllegalArgumentException("SkuId cannot be null or empty");
        }
        this.skuId = skuId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
} 