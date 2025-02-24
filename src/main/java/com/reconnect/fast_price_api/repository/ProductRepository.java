package com.reconnect.fast_price_api.repository;

import com.reconnect.fast_price_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByPriceGreaterThan(Long price);
    List<Product> findBySkuId(String skuId);
    List<Product> findByUpdatedAtAfter(LocalDateTime date);
    Optional<Product> findByIdAndLink(String id, String link);
} 