package com.kuna_backend.services;

import com.kuna_backend.dtos.cart.CartDto;
import com.kuna_backend.dtos.cart.CartItemDto;
import com.kuna_backend.enums.OrderStatus;
import com.kuna_backend.exceptions.OrderNotFoundException;
import com.kuna_backend.models.Client;
import com.kuna_backend.models.Order;
import com.kuna_backend.models.OrderItem;
import com.kuna_backend.models.Payment;
import com.kuna_backend.models.ProductVariation;
import com.kuna_backend.models.ShippingDetail;
import com.kuna_backend.repositories.OrderItemsRepository;
import com.kuna_backend.repositories.OrderRepository;
import com.kuna_backend.repositories.ProductVariationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private ShippingDetailService shippingDetailService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    public void placeOrder (Client client, Payment payment, ShippingDetail shippingDetail) {

        CartDto cartDto = cartService.listCartItems(client);

        Order newOrder = new Order();
        newOrder.setCreatedAt(new Date());
        newOrder.setClient(client);
        newOrder.setPayment(payment);
        newOrder.setTotalAmount(cartDto.getTotalCost());
        newOrder.setOrderStatus(OrderStatus.CONFIRMED);
        // Generate tracking number
        newOrder.generateTrackingNumber();

        // Check if the client has an existing shipping detail
        Optional<ShippingDetail> existingShippingDetail = shippingDetailService.getShippingDetail(client);

        existingShippingDetail.ifPresentOrElse(
                // If present, set the shipping detail from the Optional existingShippingDetail
                newOrder::setShippingDetail,
                // if absent, set the provided shippingDetail
                () -> newOrder.setShippingDetail(shippingDetail)
        );

        orderRepository.save(newOrder);

        List<CartItemDto> cartItemDtoList = cartDto.getCartItems();

        for (CartItemDto cartItemDto : cartItemDtoList) {

            OrderItem orderItem = new OrderItem();
            orderItem.setCreatedAt(new Date());
            orderItem.setPrice(cartItemDto.getProduct().getPrice());
            orderItem.setProductVariation(cartItemDto.getProductVariation());
            orderItem.setQuantity(cartItemDto.getQuantity());
            orderItem.setOrder(newOrder);

            orderItemsRepository.save(orderItem);
        }

        // Update the quantity stock of each product variation after the order is placed
        updateProductVariationQuantityStock(cartDto);

        // Delete items from cart after the client has placed the order
        cartService.deleteClientCartItems(client);
    }

    // Method for updating the quantity stock number from each Product Variation
    private void updateProductVariationQuantityStock(CartDto cartDto) {

        List<CartItemDto> cartItemDtoList = cartDto.getCartItems();

        for (CartItemDto cartItemDto : cartItemDtoList) {
            ProductVariation productVariation = cartItemDto.getProductVariation();
            int updatedQuantityStock = productVariation.getQuantityStock() - cartItemDto.getQuantity();
            productVariation.setQuantityStock(updatedQuantityStock);
            productVariationRepository.save(productVariation);
        }
    }


    public List<Order> listOrders(Client client) {
        return orderRepository.findAllByClientOrderByCreatedAtDesc(client);
    }

    public List<Order> listOrdersByStatus(OrderStatus orderStatus) {
        return orderRepository.findAllByOrderStatus(orderStatus);
    }

    public Order getOrder (Long orderId) throws OrderNotFoundException {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            return order.get();
        }
        throw new OrderNotFoundException("Order not found");
    }

    public void deleteOrder (Long id) {
        orderRepository.deleteById(id);
    }
}
