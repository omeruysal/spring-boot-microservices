package com.example.productservice.service;

import com.example.productservice.entity.Product;
import com.example.productservice.exception.ProductServiceCustomException;
import com.example.productservice.model.ProductRequest;
import com.example.productservice.model.ProductResponse;
import com.example.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public long addProduct(ProductRequest productRequest) {
        log.info("Adding Product..");

        Product product
                = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);

        log.info("Product Created");
        return product.getProductId();
    }

    public ProductResponse getProductById(long productId) {
        log.info("Get the product for productId: {}", productId);

        Product product
                = productRepository.findById(productId)
                .orElseThrow(
                        () -> new ProductServiceCustomException("Product with given id not found", "PRODUCT_NOT_FOUND"));

        ProductResponse productResponse
                = new ProductResponse();

        copyProperties(product, productResponse);

        return productResponse;
    }

    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity {} for Id: {}", quantity, productId);

        Product product
                = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException(
                        "Product with given Id not found",
                        "PRODUCT_NOT_FOUND"
                ));

        if (product.getQuantity() < quantity) {
            throw new ProductServiceCustomException(
                    "Product does not have sufficient Quantity",
                    "INSUFFICIENT_QUANTITY"
            );
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Product Quantity updated Successfully");
    }


}
