package com.kuna_backend;

import com.kuna_backend.dtos.product.ProductDto;
import com.kuna_backend.dtos.product.ProductVariationDto;
import com.kuna_backend.exceptions.ProductNotExistsException;
import com.kuna_backend.models.Category;
import com.kuna_backend.models.Product;
import com.kuna_backend.models.ProductVariation;
import com.kuna_backend.repositories.ProductRepository;
import com.kuna_backend.repositories.ProductVariationRepository;
import com.kuna_backend.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kuna_backend.enums.Color.BEIGE;
import static com.kuna_backend.enums.Size.NEWBORN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductVariationRepository productVariationRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getDtoFromProduct() {

        Product product = new Product();
        Category category = new Category();

        product.setId(1L);
        product.setName("Example Product");
        product.setPrice(9.99);
        product.setCategory(category);

        // Call the getDtoFromProduct method
        ProductDto productDto = ProductService.getDtoFromProduct(product);

        // Verify that the returned ProductDto has the expected properties
        assertEquals(product.getId(), productDto.getId());
        assertEquals(product.getName(), productDto.getName());
        assertEquals(product.getPrice(), productDto.getPrice());
        assertEquals(product.getCategory(), category);
    }

    @Test
    public void getProductFromDto() {

        ProductDto productDto = new ProductDto();
        productDto.setName("Example Product");
        productDto.setPrice(9.99);

        Category category = new Category();

        Product product = ProductService.getProductFromDto(productDto, category);

        assertEquals(productDto.getName(), product.getName());
        assertEquals(productDto.getPrice(), product.getPrice());
        assertEquals(category, product.getCategory());
    }

    @Test
    public void getProductById() throws ProductNotExistsException {

        Long productId = 1L;
        Product product = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(productId);

        assertEquals(product, result);
    }

    @Test
    public void createProduct() {

        ProductDto productDto = new ProductDto();
        Category category = new Category();

        productService.createProduct(productDto, category);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void listProducts() {

        int pageNumber = 0;
        int pageSize = 10;

        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category2.setId(2L);

        List<Product> products = new ArrayList<>();
        products.add(new Product(new ProductDto(), category1));
        products.add(new Product(new ProductDto(), category2));

        Page<Product> productPage = new PageImpl<>(products);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        List<ProductDto> result = productService.listProducts(pageNumber, pageSize);

        assertEquals(2, result.size());
        assertNotNull(result.get(0).getCategoryId());
        assertNotNull(result.get(1).getCategoryId());
    }

    @Test
    public void getProductVariationFromDto() {

        ProductVariationDto productVariationDto = new ProductVariationDto();
        productVariationDto.setSize(NEWBORN);
        productVariationDto.setColor(BEIGE);

        Product product = new Product();

        ProductVariation productVariation = ProductService.getProductVariationFromDto(productVariationDto, product);

        assertEquals(productVariationDto.getSize(), productVariation.getSize());
        assertEquals(productVariationDto.getColor(), productVariation.getColor());
        assertEquals(product, productVariation.getProduct());
    }

    @Test
    public void createProductVariationForProduct(){

        Long productId = 1L;
        Product existingProduct = new Product();

        ProductVariationDto productVariationDto = new ProductVariationDto(10, NEWBORN, BEIGE, productId);

        // Mock the getProductVariationFromDto method
        ProductVariation productVariation = ProductService.getProductVariationFromDto(productVariationDto, existingProduct);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);
        when(productVariationRepository.save(any(ProductVariation.class))).thenReturn(productVariation);

        // Call the method under test
        Product updatedProduct = productService.createProductVariationForProduct(productId, productVariationDto);

        // Verify the interactions
        verify(productRepository, times(1)).findById(productId);
        verify(productVariationRepository, times(1)).save(any(ProductVariation.class));
        verify(productRepository, times(1)).save(any(Product.class));

        // Assert the updatedProduct
        assertNotNull(updatedProduct);
        assertEquals(existingProduct, updatedProduct);
    }

    @Test
    public void getProductByValidIdWithVariations() throws ProductNotExistsException {

        Long productId = 1L;
        Product product = new Product();
        when(productRepository.findByIdWithVariations(productId)).thenReturn(Optional.of(product));

        Product result = productService.getProductByIdWithVariations(productId);

        assertEquals(product, result);
    }

    @Test
    public void getProductByInvalidIdWithVariations() throws ProductNotExistsException {

        Long productId = 1L;
        when(productRepository.findByIdWithVariations(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotExistsException.class, () -> {
            productService.getProductByIdWithVariations(productId);
        });
    }

    @Test
    public void updateProductOnly() {

        Long productId = 1L;

        Product originalProduct = new Product();
        originalProduct.setId(productId);
        originalProduct.setName("OriginalName");
        originalProduct.setPrice(30.0);
        originalProduct.setDescription("OriginalDescription");
        originalProduct.setImageUrl("original_image_url");

        ProductDto updatedProductDto = new ProductDto();
        updatedProductDto.setName("UpdatedName");
        updatedProductDto.setPrice(50.0);
        updatedProductDto.setDescription("UpdatedDescription");
        updatedProductDto.setImageUrl("updated_image_url");

        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(originalProduct));
        when(productRepository.save(any())).then(AdditionalAnswers.returnsFirstArg());

        Product updatedProduct = productService.updateProductOnly(productId, updatedProductDto);

        assertNotNull(updatedProduct);
        assertEquals(updatedProductDto.getName(), updatedProduct.getName());
        assertEquals(updatedProductDto.getPrice(), updatedProduct.getPrice());
        assertEquals(updatedProductDto.getDescription(), updatedProduct.getDescription());
        assertEquals(updatedProductDto.getImageUrl(), updatedProduct.getImageUrl());
        assertNotNull(updatedProduct.getModifiedAt());

        verify(productRepository, times(1)).findById(eq(productId));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void deleteExistingProduct() {

        Long productId = 1L;
        Product mockProduct = new Product();

        when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(mockProduct));

        boolean deletionSuccessful = productService.deleteProduct(productId);
        assertTrue(deletionSuccessful);

        // Verify that the productRepository.delete method was called with the correct argument
        verify(productRepository, times(1)).delete(mockProduct);
    }

    @Test
    public void deleteNonExistingProduct() {

        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(java.util.Optional.empty());

        boolean deletionSuccessful = productService.deleteProduct(productId);
        assertFalse(deletionSuccessful);

        // Verify that the productRepository.delete method was not called in this case
        verify(productRepository, never()).delete(any());
    }

    @Test
    public void deleteProductInvalidProductId() {

        Long invalidProductId = 0L; // Invalid product ID

        boolean deletionSuccessful = productService.deleteProduct(invalidProductId);

        assertFalse(deletionSuccessful);

        // Verify that the productRepository.delete method was not called in this case
        verify(productRepository, never()).delete(any());
    }

}
