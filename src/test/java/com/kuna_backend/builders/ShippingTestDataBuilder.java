package com.kuna_backend.builders;

import com.kuna_backend.dtos.shipping.ShippingDetailDto;
import com.kuna_backend.models.Client;
import com.kuna_backend.models.ShippingDetail;

public class ShippingTestDataBuilder {
    public static Client createClientWithShippingDetail(ShippingDetail shippingDetail) {
        Client client = new Client();
        client.setShippingDetail(shippingDetail);
        return client;
    }

    public static ShippingDetailDto createShippingDetailDto() {
        return new ShippingDetailDto("John Doe", "123 Main St", "NYC",
                "12345", "USA", "456-7890");
    }

}
