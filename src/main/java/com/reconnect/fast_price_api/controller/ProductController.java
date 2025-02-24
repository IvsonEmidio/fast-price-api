package com.reconnect.fast_price_api.controller;

import com.reconnect.fast_price_api.dto.CreateProductRequest;
import com.reconnect.fast_price_api.dto.UpdateProductRequest;
import com.reconnect.fast_price_api.dto.GetProductRequest;
import com.reconnect.fast_price_api.model.Product;
import com.reconnect.fast_price_api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/find")
    public ResponseEntity<?> getProductById(@RequestBody GetProductRequest request) {
        try {
            return productService.getProductById(request)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest request) {
        try {
            List<Product> savedProducts = productService.createProduct(request);
            return ResponseEntity.ok(savedProducts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProduct(@RequestBody UpdateProductRequest request) {
        try {
            return productService.updateProduct(request)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 