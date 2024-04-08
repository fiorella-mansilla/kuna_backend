package com.kuna_backend.services;

import com.kuna_backend.dtos.product.ProductDto;
import com.kuna_backend.dtos.product.ProductVariationDto;
import com.kuna_backend.exceptions.ProductNotExistsException;
import com.kuna_backend.models.Category;
import com.kuna_backend.models.Product;
import com.kuna_backend.models.ProductVariation;
import com.kuna_backend.repositories.ProductRepository;
import com.kuna_backend.repositories.ProductVariationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    public static ProductDto getDtoFromProduct(Product product) {
        ProductDto productDto = new ProductDto(product);
        return productDto;
    }

    public static Product getProductFromDto(ProductDto productDto, Category category) {
        Product product = new Product(productDto, category);
        return product;
    }

    // Creates only the Product with its respective Category
    public void createProduct(ProductDto productDto, Category category) {
        Product product = getProductFromDto(productDto, category);
        productRepository.save(product);
    }

    // Retrieves a specific Product by ID without its Variations
    public Product getProductById(Long productId) throws ProductNotExistsException {
        // Fetch the product by ID
        Optional<Product> optionalProduct = productRepository.findById(productId);
        // Check if the product exists
        if (optionalProduct.isEmpty()) {
            throw new ProductNotExistsException("The Product id is invalid: " + productId);
        }
        return optionalProduct.get();
    }

    // Retrieves a List of all the Products with Pagination
    public List<ProductDto> listProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product: productPage) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    // Update only the attributes from a specific Product by ID
    public Product updateProductOnly(Long productId, ProductDto updatedProductDto) {

        // Retrieve the specific Product
        Product product = getProductById(productId);

        if (updatedProductDto.getName() != null) {
            product.setName(updatedProductDto.getName());
        }
        if (updatedProductDto.getPrice() != 0) {
            product.setPrice(updatedProductDto.getPrice());
        }
        if (updatedProductDto.getDescription() != null) {
            product.setDescription(updatedProductDto.getDescription());
        }
        if (updatedProductDto.getImageUrl() != null) {
            product.setImageUrl(updatedProductDto.getImageUrl());
        }

        // Update timestamp
        product.setModifiedAt(LocalDateTime.now());

        // Save and return the updated Product
        return productRepository.save(product);
    }

    public boolean deleteProduct (Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if(optionalProduct.isPresent()) {
            productRepository.delete(optionalProduct.get());
            return true; // Deletion successful
        } else {
            return false; // Product not found, deletion unsuccessful
        }
    }

    // Product Variation Creation - as a Product update
    public static ProductVariation getProductVariationFromDto(ProductVariationDto productVariationDto, Product product) {
        ProductVariation productVariation = new ProductVariation(productVariationDto, product);
        return productVariation;
    }

    // Creates a Product Variation assigned to a specific Product when updating it.
    public Product createProductVariationForProduct (Long productId, ProductVariationDto productVariationDto) {

        // Retrieve the specific Product based on the ID.
        Product product = getProductById(productId);

        // Create a new Product Variation entity and populate with the corresponding data from ProductVariationDto
        ProductVariation productVariation = getProductVariationFromDto(productVariationDto, product);

        // Save the new ProductVariation entity in the database, which will generate a unique ID for it.
        productVariationRepository.save(productVariation);

        // Add the ProductVariation to the Product's collection
        product.getProductVariations().add(productVariation);

        // Save and return the updated Product
        return productRepository.save(product);
    }

    // Retrieves a specific Product by ID with its Variations
    public Product getProductByIdWithVariations(Long productId) throws ProductNotExistsException {
        // Fetch the product by ID
        Optional<Product> optionalProduct = productRepository.findByIdWithVariations(productId);
        // Check if the product exists
        if (optionalProduct.isEmpty()) {
            throw new ProductNotExistsException("The Product id is invalid: " + productId);
        }
        return optionalProduct.get();
    }
}
