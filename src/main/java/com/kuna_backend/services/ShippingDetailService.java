package com.kuna_backend.services;

import com.kuna_backend.dtos.shipping.ShippingDetailDto;
import com.kuna_backend.models.Client;
import com.kuna_backend.models.ShippingDetail;
import com.kuna_backend.repositories.ShippingDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShippingDetailService {

    private final ShippingDetailRepository shippingDetailRepository;

    @Autowired
    public ShippingDetailService(ShippingDetailRepository shippingDetailRepository) {
        this.shippingDetailRepository = shippingDetailRepository;
    }

    public ShippingDetail addShippingDetail(ShippingDetailDto shippingDetailDto, Client client) {

        // Check if the client has a shipping detail
        Optional<ShippingDetail> optionalShippingDetail = getShippingDetail(client);

        if(optionalShippingDetail.isPresent()){
            // Update the existing shipping detail in case there is new information
            ShippingDetail existingShippingDetail = optionalShippingDetail.get();

            existingShippingDetail.setFullName(shippingDetailDto.getFullName());
            existingShippingDetail.setAddress(shippingDetailDto.getAddress());
            existingShippingDetail.setCity(shippingDetailDto.getCity());
            existingShippingDetail.setZipCode(shippingDetailDto.getZipCode());
            existingShippingDetail.setCountry(shippingDetailDto.getCountry());
            existingShippingDetail.setPhone(shippingDetailDto.getPhone());

            return shippingDetailRepository.save(existingShippingDetail);

        } else {
            // Create a new shipping detail for the Client
            ShippingDetail newShippingDetail = createNewShippingDetail(shippingDetailDto, client);

            return shippingDetailRepository.save(newShippingDetail);
        }
    }

    private ShippingDetail createNewShippingDetail(ShippingDetailDto shippingDetailDto, Client client) {

        ShippingDetail newShippingDetail = new ShippingDetail();

        newShippingDetail.setFullName(shippingDetailDto.getFullName());
        newShippingDetail.setAddress(shippingDetailDto.getAddress());
        newShippingDetail.setCity(shippingDetailDto.getCity());
        newShippingDetail.setZipCode(shippingDetailDto.getZipCode());
        newShippingDetail.setCountry(shippingDetailDto.getCountry());
        newShippingDetail.setPhone(shippingDetailDto.getPhone());
        newShippingDetail.setClient(client);
        return newShippingDetail;
    }

    public List<ShippingDetail> getAllShippingDetails() {
        return shippingDetailRepository.findAll();
    }

    public Optional<ShippingDetail> getShippingDetail(Client client) {
        if (client != null && client.getShippingDetail() != null) {
           return shippingDetailRepository.findById(client.getShippingDetail().getId());
        }
        return Optional.empty();
    }
}
