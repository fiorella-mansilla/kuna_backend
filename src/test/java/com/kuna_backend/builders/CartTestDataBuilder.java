package com.kuna_backend.builders;

import com.kuna_backend.dtos.cart.CartItemDto;
import com.kuna_backend.models.Cart;
import com.kuna_backend.models.Client;
import com.kuna_backend.models.Product;
import com.kuna_backend.models.ProductVariation;

import java.util.List;

public class CartTestDataBuilder {

    public static List<Cart> createSampleCartItems(Client client) {
        Product product1 = createProductWithPrice(10.0);
        Product product2 = createProductWithPrice(20.0);

        Cart cart1 = createCart(product1, 2, client);
        Cart cart2 = createCart(product2, 1, client);

        return List.of(cart1, cart2);
    }

    public static Product createProductWithPrice(double price) {
        Product product = new Product();
        product.setPrice(price);
        return product;
    }

    public static Cart createCart(Product product, int quantity, Client client) {
        ProductVariation productVariation = new ProductVariation();
        productVariation.setProduct(product);

        return new Cart(productVariation, quantity, client);
    }

    public static CartItemDto createCartItemDto(Cart cart) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProduct(cart.getProductVariation().getProduct());
        cartItemDto.setProductVariation(cart.getProductVariation());
        cartItemDto.setQuantity(cart.getQuantity());
        return cartItemDto;
    }
}
