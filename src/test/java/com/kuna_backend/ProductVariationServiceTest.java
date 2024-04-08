package com.kuna_backend;

import com.kuna_backend.builders.ProductVariationTestDataBuilder;
import com.kuna_backend.dtos.product.ProductVariationDto;
import com.kuna_backend.exceptions.ProductNotExistsException;
import com.kuna_backend.models.ProductVariation;
import com.kuna_backend.repositories.ProductVariationRepository;
import com.kuna_backend.services.ProductVariationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class ProductVariationServiceTest {

    @Mock
    private ProductVariationRepository productVariationRepository;

    @InjectMocks
    private ProductVariationService productVariationService;

    @Test
    public void getDtoFromProductVariation() {

        ProductVariation productVariation = ProductVariationTestDataBuilder.createProductVariation();

        ProductVariationDto productVariationDto = ProductVariationService.getDtoFromProductVariation(productVariation);

        assertEquals(productVariation.getId(), productVariationDto.getId());
        assertEquals(productVariation.getSize(), productVariationDto.getSize());
        assertEquals(productVariation.getProduct().getId(), productVariationDto.getProductId());
    }

    @Test
    public void listProductVariations() {

        List<ProductVariation> expectedProductVariations = ProductVariationTestDataBuilder.createProductVariations();
        when(productVariationRepository.findAll()).thenReturn(expectedProductVariations);

        List<ProductVariation> productVariations = productVariationService.listProductVariations();

        assertEquals(expectedProductVariations.size(), productVariations.size());
    }

    @Test
    public void getProductVariationById_WhenExists_ShouldReturnProductVariation() throws ProductNotExistsException {

        Long productVariationId = 1L;
        ProductVariation expectedProductVariation = ProductVariationTestDataBuilder.createProductVariation();
        when(productVariationRepository.findById(productVariationId)).thenReturn(Optional.of(expectedProductVariation));

        ProductVariation productVariation = productVariationService.getProductVariationById(productVariationId);

        assertEquals(expectedProductVariation, productVariation);
    }

    @Test
    public void getProductVariationById_WhenNotExists_ShouldThrowProductNotExistsException() {

        Long productVariationId = 1L;
        when(productVariationRepository.findById(productVariationId)).thenReturn(Optional.empty());

        assertThrows(ProductNotExistsException.class,
                () -> productVariationService.getProductVariationById(productVariationId));
    }

    @Test
    public void updateProductVariation_WhenSuccessful_ShouldUpdateProductVariation() {

        Long productVariationId = 1L;
        ProductVariation existingProductVariation = ProductVariationTestDataBuilder.createProductVariation();
        ProductVariationDto updatedVariationDto = new ProductVariationDto();
        updatedVariationDto.setQuantityStock(10); // Only the quantityStock will be updated. Color is not updated in this test
        when(productVariationRepository.save(eq(existingProductVariation))).thenReturn(existingProductVariation);
        when(productVariationRepository.findById(productVariationId)).thenReturn(Optional.of(existingProductVariation));

        ProductVariation updatedProductVariation = productVariationService.updateProductVariation(productVariationId, updatedVariationDto);

        assertNotNull(updatedProductVariation);
        assertEquals(existingProductVariation.getQuantityStock(), updatedProductVariation.getQuantityStock()); // Check the updated attribute
        assertEquals(existingProductVariation.getColor(), updatedProductVariation.getColor()); // Check that the color remains the same

        verify(productVariationRepository, times(1)).findById(productVariationId);
        // Verify that the productVariationRepository.save method was called with the correct argument
        verify(productVariationRepository, times(1)).save(updatedProductVariation);
    }

    @Test
    public void updateProductVariation_WhenNotExists_ShouldThrowProductNotExistsException() {

        Long productVariationId = 1L;
        when(productVariationRepository.findById(productVariationId)).thenReturn(Optional.empty());
        ProductVariationDto updatedVariationDto = new ProductVariationDto();

        ProductNotExistsException exception = assertThrows(ProductNotExistsException.class,
                () -> productVariationService.updateProductVariation(productVariationId, updatedVariationDto));

        assertEquals("Product Variation not found with ID: " + productVariationId, exception.getMessage());
        verify(productVariationRepository, times(1)).findById(productVariationId);
        verify(productVariationRepository, never()).save(any(ProductVariation.class));
    }

    @Test
    void updateProductVariation_WhenDtoIsNull_ShouldThrowIllegalArgumentException() {

        Long productVariationId = 1L;

        assertThrows(IllegalArgumentException.class, () -> {
            productVariationService.updateProductVariation(productVariationId, null);
        });
    }


    @Test
    public void deleteProductVariation_WhenExists_ShouldReturnTrueAndDeleteProductVariation() {

        Long productVariationId = 1L;
        ProductVariation productVariation = ProductVariationTestDataBuilder.createProductVariation();
        when(productVariationRepository.findById(productVariationId)).thenReturn(java.util.Optional.of(productVariation));

        boolean deletionSuccessful = productVariationService.deleteProductVariation(productVariationId);

        assertTrue(deletionSuccessful);
        verify(productVariationRepository, times(1)).delete(productVariation);
    }

    @Test
    public void deleteProductVariation_WhenNotExists_ShouldReturnFalseForNonExistingVariation() {

        Long productVariationId = 1L;
        when(productVariationRepository.findById(productVariationId)).thenReturn(java.util.Optional.empty());

        boolean deletionSuccessful = productVariationService.deleteProductVariation(productVariationId);

        assertFalse(deletionSuccessful);
        verify(productVariationRepository, never()).delete(any());
    }

    @Test
    public void deleteProductVariation_WhenInvalidId_ShouldReturnFalseAndNotDeleteProductVariation() {

        Long invalidProductVariationId = 0L;

        boolean deletionSuccessful = productVariationService.deleteProductVariation(invalidProductVariationId);

        assertFalse(deletionSuccessful);
        verify(productVariationRepository, never()).delete(any());
    }

}
