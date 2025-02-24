package com.reconnect.fast_price_api.service;

import com.reconnect.fast_price_api.dto.AliexpressResponse;
import com.reconnect.fast_price_api.dto.CreateProductRequest;
import com.reconnect.fast_price_api.dto.UpdateProductRequest;
import com.reconnect.fast_price_api.dto.GetProductRequest;
import com.reconnect.fast_price_api.model.Product;
import com.reconnect.fast_price_api.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://aliexpress-datahub.p.rapidapi.com/item_detail_6?itemId=";
    private static final String API_HOST = "aliexpress-datahub.p.rapidapi.com";
    private static final String API_KEY = "6dac46ee85msh0c74940a57363a5p1d96bejsn5bf5c83fda1a";

    public List<Product> getAllProducts() {
        logger.debug("Fetching all products");
        List<Product> products = productRepository.findAll();
        logger.info("Found {} products", products.size());
        return products;
    }

    public Optional<Product> getProductById(GetProductRequest request) {
        logger.debug("Searching for product with id: {} and link: {}", request.getId(), request.getLink());
        
        if (request.getId() == null || request.getId().trim().isEmpty()) {
            logger.error("Product search failed: Id is required");
            throw new IllegalArgumentException("Id is required");
        }
        if (request.getLink() == null || request.getLink().trim().isEmpty()) {
            logger.error("Product search failed: Link is required");
            throw new IllegalArgumentException("Link is required");
        }
        
        Optional<Product> product = productRepository.findByIdAndLink(request.getId(), request.getLink());
        if (product.isPresent()) {
            logger.info("Found product with id: {} and link: {}", request.getId(), request.getLink());
        } else {
            logger.info("No product found with id: {} and link: {}", request.getId(), request.getLink());
        }
        return product;
    }

    public List<Product> createProduct(CreateProductRequest request) {
        logger.debug("Creating new product from link: {}", request.getLink());
        
        String itemId = extractItemId(request.getLink());
        if (itemId == null) {
            logger.error("Product creation failed: Invalid AliExpress link format");
            throw new IllegalArgumentException("Invalid AliExpress link format");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-rapidapi-host", API_HOST);
            headers.set("x-rapidapi-key", API_KEY);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            logger.debug("Making API call to AliExpress for itemId: {}", itemId);
            
            AliexpressResponse response = restTemplate.exchange(
                API_URL + itemId,
                HttpMethod.GET,
                entity,
                AliexpressResponse.class
            ).getBody();

            if (response == null || response.getResult() == null || 
                response.getResult().getItem() == null || 
                response.getResult().getItem().getSku() == null ||
                response.getResult().getItem().getSku().getBase() == null ||
                response.getResult().getItem().getSku().getBase().isEmpty()) {
                logger.error("Product creation failed: Invalid response from AliExpress API");
                throw new IllegalArgumentException("Failed to fetch product details from AliExpress");
            }

            List<Product> savedProducts = new ArrayList<>();
            
            for (AliexpressResponse.Base sku : response.getResult().getItem().getSku().getBase()) {
                Optional<Product> existingProduct = productRepository.findByIdAndLink(sku.getSkuAttr(), request.getLink());
                
                Product product;
                if (existingProduct.isPresent()) {
                    product = existingProduct.get();
                    logger.debug("Updating existing product with id: {} and skuId: {}", product.getId(), product.getSkuId());
                } else {
                    product = new Product();
                    product.setId(sku.getSkuAttr());
                    product.setLink(request.getLink());
                    product.setSkuId(sku.getSkuId());
                    logger.debug("Creating new product with id: {} and skuId: {}", sku.getSkuAttr(), sku.getSkuId());
                }
                
                Product savedProduct = productRepository.save(product);
                savedProducts.add(savedProduct);
                logger.info("Saved product with id: {} and skuId: {}", savedProduct.getId(), savedProduct.getSkuId());
            }
            
            logger.info("Successfully processed {} products from link: {}", savedProducts.size(), request.getLink());
            return savedProducts;
            
        } catch (Exception e) {
            logger.error("Error processing products: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Optional<Product> updateProduct(UpdateProductRequest request) {
        logger.debug("Updating product with id: {} and link: {}", request.getId(), request.getLink());
        
        if (request.getId() == null || request.getId().trim().isEmpty()) {
            logger.error("Product update failed: Id is required");
            throw new IllegalArgumentException("Id is required");
        }
        if (request.getLink() == null || request.getLink().trim().isEmpty()) {
            logger.error("Product update failed: Link is required");
            throw new IllegalArgumentException("Link is required");
        }
        
        Optional<Product> result = productRepository.findByIdAndLink(request.getId(), request.getLink())
                .map(product -> {
                    product.setPrice(request.getPrice());
                    Product updatedProduct = productRepository.save(product);
                    logger.info("Successfully updated price for product with id: {} and link: {}", request.getId(), request.getLink());
                    return updatedProduct;
                });
                
        if (result.isEmpty()) {
            logger.info("No product found to update with id: {} and link: {}", request.getId(), request.getLink());
        }
        
        return result;
    }

    private String extractItemId(String link) {
        Pattern pattern = Pattern.compile("item/(\\d+)\\.html");
        Matcher matcher = pattern.matcher(link);
        if (matcher.find()) {
            String itemId = matcher.group(1);
            logger.debug("Extracted itemId: {} from link: {}", itemId, link);
            return itemId;
        }
        logger.debug("Could not extract itemId from link: {}", link);
        return null;
    }
} 