package com.kuna_backend.builders;

import com.kuna_backend.dtos.checkout.CheckoutItemDto;
import com.kuna_backend.models.Cart;
import com.kuna_backend.models.Product;
import com.kuna_backend.models.ProductVariation;

public class PaymentTestDataBuilder {

    public static CheckoutItemDto buildCheckoutItemDto() {
        CheckoutItemDto checkoutItemDto = new CheckoutItemDto();
        checkoutItemDto.setProductName("Product 1");
        checkoutItemDto.setPrice(10.0);
        checkoutItemDto.setQuantity(2);
        return checkoutItemDto;
    }

    public static Cart buildCart() {
        Cart cart = new Cart();

        Product product = buildProduct();
        ProductVariation productVariation = buildProductVariation();
        productVariation.setProduct(product);

        cart.setQuantity(4);
        cart.setProductVariation(productVariation);

        return cart;
    }

    public static Product buildProduct() {
        Product product = new Product();
        product.setName("Body");
        product.setPrice(20.0);
        return product;
    }

    public static ProductVariation buildProductVariation() {
        ProductVariation productVariation = new ProductVariation();
        Product product = new Product();
        productVariation.setId(1L);
        productVariation.setProduct(product);
        return productVariation;
    }
}
