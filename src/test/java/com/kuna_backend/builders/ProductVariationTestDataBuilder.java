package com.kuna_backend.builders;

import com.kuna_backend.dtos.product.ProductVariationDto;
import com.kuna_backend.enums.Size;
import com.kuna_backend.models.Product;
import com.kuna_backend.models.ProductVariation;

import java.util.ArrayList;
import java.util.List;

public class ProductVariationTestDataBuilder {

    public static ProductVariation createProductVariation() {
        Product product = new Product();
        product.setId(1L);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setId(1L);
        productVariation.setSize(Size.NEWBORN);
        productVariation.setProduct(product);
        return productVariation;
    }

    public static List<ProductVariation> createProductVariations() {
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        List<ProductVariation> productVariations = new ArrayList<>();
        productVariations.add(new ProductVariation(new ProductVariationDto(), product1));
        productVariations.add(new ProductVariation(new ProductVariationDto(), product2));
        return productVariations;
    }

}
