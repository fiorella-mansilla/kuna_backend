package com.kuna_backend;

import com.kuna_backend.models.OrderItem;
import com.kuna_backend.repositories.OrderItemsRepository;
import com.kuna_backend.services.OrderItemsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderItemsServiceTest {

    @Mock
    private OrderItemsRepository orderItemsRepository;

    @InjectMocks
    private OrderItemsService orderItemsService;

    @Test
    public void addOrderedItems() {

        OrderItem orderItem = new OrderItem();

        orderItemsService.addOrderedItems(orderItem);

        verify(orderItemsRepository, times(1)).save(orderItem);
    }
}


