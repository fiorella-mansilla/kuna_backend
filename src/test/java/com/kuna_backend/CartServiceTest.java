package com.kuna_backend;

import com.kuna_backend.builders.CartTestDataBuilder;
import com.kuna_backend.dtos.cart.AddToCartDto;
import com.kuna_backend.dtos.cart.CartDto;
import com.kuna_backend.exceptions.CartItemNotExistException;
import com.kuna_backend.models.Cart;
import com.kuna_backend.models.Client;
import com.kuna_backend.models.ProductVariation;
import com.kuna_backend.repositories.CartRepository;
import com.kuna_backend.services.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    public void addItemToCart() {

        AddToCartDto addToCartDto = new AddToCartDto();
        ProductVariation productVariation = new ProductVariation();
        Client client = new Client();
        Cart expectedCart = new Cart(productVariation, addToCartDto.getQuantity(), client);

        when(cartRepository.save(any(Cart.class))).thenReturn(expectedCart);

        cartService.addToCart(addToCartDto, productVariation, client);

        assertEquals(productVariation, expectedCart.getProductVariation());
        assertEquals(addToCartDto.getQuantity(), expectedCart.getQuantity());
        assertEquals(client, expectedCart.getClient());
    }

    @Test
    public void listCartItems() {

        Client client = new Client();
        List<Cart> cartList = CartTestDataBuilder.createSampleCartItems(client);

        when(cartRepository.findAllByClientOrderByCreatedAtDesc(client)).thenReturn(cartList);

        CartDto cartDto = cartService.listCartItems(client);

        assertEquals(2, cartDto.getCartItems().size());
        assertEquals(2, cartDto.getCartItems().get(0).getQuantity());
        assertEquals(1, cartDto.getCartItems().get(1).getQuantity());
        assertEquals(40.0, cartDto.getTotalCost(), 0.0001);
    }

    @Test
    public void deleteCartItem_WhenExists_ShouldDeleteCartItem() throws CartItemNotExistException {

        Long id = 1L;
        when(cartRepository.existsById(id)).thenReturn(true);

        cartService.deleteCartItem(id, 123L);

        verify(cartRepository, times(1)).deleteById(id);
    }

    @Test
    public void deleteCartItem_WhenNotExists_ShouldThrowCartItemNotExistException() {

        Long id = 1L;
        when(cartRepository.existsById(id)).thenReturn(false);

        assertThrows(CartItemNotExistException.class, () -> cartService.deleteCartItem(id, 123L));

        verify(cartRepository, never()).deleteById(id);
    }

    @Test
    public void deleteAllCartItems() {

        cartService.deleteCartItems(123L);

        verify(cartRepository, times(1)).deleteAll();
    }

    @Test
    public void deleteAllCartItemsForClient() {

        Client client = new Client();

        cartService.deleteClientCartItems(client);

        verify(cartRepository, times(1)).deleteByClient(client);
    }
}
