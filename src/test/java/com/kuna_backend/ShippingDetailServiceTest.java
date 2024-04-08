package com.kuna_backend;

import com.kuna_backend.builders.ShippingTestDataBuilder;
import com.kuna_backend.dtos.shipping.ShippingDetailDto;
import com.kuna_backend.models.Client;
import com.kuna_backend.models.ShippingDetail;
import com.kuna_backend.repositories.ShippingDetailRepository;
import com.kuna_backend.services.ShippingDetailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShippingDetailServiceTest {
    @Mock
    private ShippingDetailRepository shippingDetailRepository;

    @Mock
    private ShippingDetail existingShippingDetail;

    @InjectMocks
    private ShippingDetailService shippingDetailService;

    @Test
    public void updateShippingDetail_ForExistingShippingDetailOfClient_ShouldUpdateExistingDetails() {

        Long id = 1L;
        Client client = ShippingTestDataBuilder.createClientWithShippingDetail(existingShippingDetail);
        ShippingDetailDto shippingDetailDto = ShippingTestDataBuilder.createShippingDetailDto();

        when(client.getShippingDetail().getId()).thenReturn(id);
        when(shippingDetailRepository.findById(id)).thenReturn(java.util.Optional.of(existingShippingDetail));

        shippingDetailService.addShippingDetail(shippingDetailDto, client);

        verify(shippingDetailRepository).save(existingShippingDetail);
        verify(existingShippingDetail).setFullName(shippingDetailDto.getFullName());
        verify(existingShippingDetail).setAddress(shippingDetailDto.getAddress());
        verify(existingShippingDetail).setCity(shippingDetailDto.getCity());
        verify(existingShippingDetail).setZipCode(shippingDetailDto.getZipCode());
        verify(existingShippingDetail).setCountry(shippingDetailDto.getCountry());
        verify(existingShippingDetail).setPhone(shippingDetailDto.getPhone());
    }

    @Test
    public void addShippingDetail_ForNonExistingShippingDetailOfClient_ShouldSaveNewDetails() {

        Client client = ShippingTestDataBuilder.createClientWithShippingDetail(null);
        ShippingDetailDto shippingDetailDto = ShippingTestDataBuilder.createShippingDetailDto();

        shippingDetailService.addShippingDetail(shippingDetailDto, client);

        verify(shippingDetailRepository, times(1)).save(any(ShippingDetail.class));
    }

    @Test
    public void getAllShippingDetails() {

        List<ShippingDetail> expectedShippingDetail = new ArrayList<>();
        when(shippingDetailRepository.findAll()).thenReturn(expectedShippingDetail);

        List<ShippingDetail> shippingDetail = shippingDetailService.getAllShippingDetails();

        verify(shippingDetailRepository).findAll();
        assertEquals(expectedShippingDetail, shippingDetail);
    }

    @Test
    public void getShippingDetail_WithValidId_ShouldReturnShippingDetail() {

        Client client = ShippingTestDataBuilder.createClientWithShippingDetail(new ShippingDetail());
        when(shippingDetailRepository.findById(client.getShippingDetail().getId())).thenReturn(Optional.of(client.getShippingDetail()));

        Optional<ShippingDetail> shippingDetail = shippingDetailService.getShippingDetail(client);

        verify(shippingDetailRepository).findById(client.getShippingDetail().getId());
        assertEquals(client.getShippingDetail(), shippingDetail.orElse(null));
    }

    @Test
    public void getShippingDetail_WithInvalidId_ShouldReturnEmptyOptional() {

        Long invalidId = 99L;
        Client client = ShippingTestDataBuilder.createClientWithShippingDetail(null);
        client.setId(invalidId);

        Optional<ShippingDetail> result = shippingDetailService.getShippingDetail(client);

        assertEquals(Optional.empty(), result);
        verify(shippingDetailRepository, never()).findById(invalidId);
    }

}
