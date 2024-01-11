package com.kuna_backend;

import com.kuna_backend.common.ApiResponse;
import com.kuna_backend.controllers.ProductController;
import com.kuna_backend.dtos.product.ProductDto;
import com.kuna_backend.models.Category;
import com.kuna_backend.models.Product;
import com.kuna_backend.services.CategoryService;
import com.kuna_backend.services.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductController productController;

    @Test
    public void getProducts_ShouldReturnProductList() {

        List<ProductDto> productList = new ArrayList<>();
        when(productService.listProducts(0,10)).thenReturn(productList);

        ResponseEntity<List<ProductDto>> response = productController.getProducts(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productList, response.getBody());

    }

    @Test
    public void getProductByIdWithVariations_shouldReturnProductById() {

        Integer productId = 1;
        Product product = new Product();
        when(productService.getProductByIdWithVariations(productId)).thenReturn(product);

        ResponseEntity<Product> response = productController.getProductByIdWithVariations(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
    }

    @Test
    public void getProductByIdWithVariations_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {

        Integer productId = 1;
        when(productService.getProductByIdWithVariations(productId)).thenThrow(new NoSuchElementException());

        ResponseEntity<Product> response = productController.getProductByIdWithVariations(productId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createProduct_shouldReturnSuccessResponse() {

        ProductDto productDto = new ProductDto();
        productDto.setCategoryId(1);

        Optional<Category> optionalCategory = Optional.of(new Category());
        when(categoryService.readCategory(productDto.getCategoryId())).thenReturn(optionalCategory);

        ResponseEntity<ApiResponse> response = productController.createProduct(productDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(new ApiResponse(true, "The Product has been created"), response.getBody());

        // Verify that the productService.createProduct method was called with the correct arguments
        verify(productService, times(1)).createProduct(eq(productDto), any(Category.class));

    }

}