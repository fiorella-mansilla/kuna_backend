package com.kuna_backend.builders;

import com.kuna_backend.dtos.client.SignupDto;
import com.kuna_backend.models.Client;

public class ClientTestDataBuilder {

    public static Client createClient() {
        return new Client("John", "Doe", "john@example.com", "password");
    }

    public static SignupDto createSignupDto() {
        return new SignupDto("John", "Doe", "john@example.com", "password");
    }
}
