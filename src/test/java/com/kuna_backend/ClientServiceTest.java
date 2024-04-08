package com.kuna_backend;

import com.kuna_backend.builders.ClientTestDataBuilder;
import com.kuna_backend.dtos.ResponseDto;
import com.kuna_backend.dtos.client.SignInDto;
import com.kuna_backend.dtos.client.SignInResponseDto;
import com.kuna_backend.dtos.client.SignupDto;
import com.kuna_backend.exceptions.AuthenticationFailException;
import com.kuna_backend.exceptions.CustomException;
import com.kuna_backend.models.AuthenticationToken;
import com.kuna_backend.models.Client;
import com.kuna_backend.repositories.ClientRepository;
import com.kuna_backend.services.AuthenticationService;
import com.kuna_backend.services.ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private ClientService clientService;

    private final String testEmail = "john@example.com";
    private final String testPassword = "password";

    @Test
    public void signUp_WithNonExistingEmail_ShouldCreateNewClient() throws CustomException {

        SignupDto signupDto = ClientTestDataBuilder.createSignupDto();
        when(clientRepository.findByEmail(signupDto.getEmail())).thenReturn(null);
        when(clientRepository.save(any(Client.class))).thenReturn(new Client());
        doNothing().when(authenticationService).saveConfirmationToken(any(AuthenticationToken.class));

        ResponseDto responseDto = clientService.signUp(signupDto);

        assertNotNull(responseDto);
        assertEquals("success", responseDto.getStatus());
        assertEquals("The user has been successfully created.", responseDto.getMessage());
        verify(clientRepository).findByEmail(testEmail);
        verify(clientRepository).save(any(Client.class));
        verify(authenticationService).saveConfirmationToken(any(AuthenticationToken.class));
    }

    @Test
    public void signUp_WithExistingEmail_ShouldThrowCustomException() {

        Client existingClient = ClientTestDataBuilder.createClient();
        when(clientRepository.findByEmail(testEmail)).thenReturn(existingClient);
        SignupDto signupDto = ClientTestDataBuilder.createSignupDto();

        assertThrows(CustomException.class, () -> clientService.signUp(signupDto));
        verify(clientRepository).findByEmail(testEmail);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void signUp_WithExceptionOnSave_ShouldThrowCustomException() {

        SignupDto signupDto = ClientTestDataBuilder.createSignupDto();
        when(clientRepository.findByEmail(signupDto.getEmail())).thenReturn(null);
        doThrow(new RuntimeException("Failed to save")).when(clientRepository).save(any(Client.class));

        assertThrows(CustomException.class, () -> clientService.signUp(signupDto));
        verify(clientRepository).findByEmail(testEmail);
        verify(clientRepository).save(any(Client.class));
        verify(authenticationService, never()).saveConfirmationToken(any(AuthenticationToken.class));
    }

    @Test
    public void signIn_WithValidCredentials_ShouldReturnToken() {

        Client client = ClientTestDataBuilder.createClient();
        client.setPassword(passwordEncoder.encode(testPassword));

        SignInDto signInDto = new SignInDto();
        signInDto.setEmail(testEmail);
        signInDto.setPassword(testPassword);

        AuthenticationToken token = new AuthenticationToken();
        token.setToken("token");

        when(clientRepository.findByEmail(testEmail)).thenReturn(client);
        when(passwordEncoder.matches(testPassword, client.getPassword())).thenReturn(true);
        when(authenticationService.getToken(client)).thenReturn(token);

        SignInResponseDto response = clientService.signIn(signInDto);

        assertEquals("Success", response.getStatus());
        assertEquals(token.getToken(), response.getToken());
        verify(clientRepository).findByEmail(testEmail);
        verify(passwordEncoder).matches(testPassword, client.getPassword());
        verify(authenticationService).getToken(client);
    }

    @Test
    public void signIn_WithInvalidEmail_ShouldThrowAuthenticationFailException() {

        SignInDto signInDto = new SignInDto("invalid_email@example.com", testPassword);
        when(clientRepository.findByEmail(signInDto.getEmail())).thenReturn(null);

        assertThrows(AuthenticationFailException.class, () -> clientService.signIn(signInDto));
        verify(clientRepository).findByEmail(signInDto.getEmail());
    }

    @Test
    public void signIn_WithInvalidPassword_ShouldThrowAuthenticationFailException() {

        SignInDto signInDto = new SignInDto(testEmail, "invalid_password");
        Client mockClient = ClientTestDataBuilder.createClient();
        mockClient.setPassword("$2a$10$OrsbB5HilcA8TubrEm9nCOB9TQ/NPlUhCjSdcBxEnEX9TN1pL7ehG");
        when(clientRepository.findByEmail(signInDto.getEmail())).thenReturn(mockClient);
        when(passwordEncoder.matches(signInDto.getPassword(), mockClient.getPassword())).thenReturn(false);

        AuthenticationFailException exception = assertThrows(AuthenticationFailException.class, () -> clientService.signIn(signInDto));
        assertEquals("This password is invalid. Please, try again.", exception.getMessage());
        verify(clientRepository).findByEmail(signInDto.getEmail());
        verify(passwordEncoder).matches(signInDto.getPassword(), mockClient.getPassword());
    }

    @Test
    public void hashPassword_WithValidPassword_ShouldReturnHashedPassword() {

        String hashedPassword = clientService.hashPassword(testPassword);
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        Assertions.assertNotNull(hashedPassword);
        Assertions.assertTrue(passwordEncoder.matches(testPassword, hashedPassword));
    }

    @Test
    public void hashPassword_WithBlankPassword_ShouldThrowIllegalArgumentException() {

        String blank_password = "";

        assertThrows(IllegalArgumentException.class, () -> clientService.hashPassword(blank_password));
    }

    @Test
    public void getAllClients() {

        when(clientRepository.findAll()).thenReturn(java.util.List.of(new Client(), new Client()));

        List<Client> clients = clientService.getAllClients();

        assertNotNull(clients);
        assertEquals(2, clients.size());
        verify(clientRepository).findAll();
    }

    @Test
    public void getClient_WithValidId_ShouldReturnClient() {

        Long clientId = 1L;
        Client client = ClientTestDataBuilder.createClient();
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        Client result = clientService.getClient(clientId);

        assertNotNull(result);
        assertEquals(client, result);
        verify(clientRepository).findById(clientId);
    }

    @Test
    public void getClient_WithInvalidId_ShouldThrowNoSuchElementException() {

        Long clientId = 1L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> clientService.getClient(clientId));
        verify(clientRepository).findById(clientId);
    }

    @Test
    public void deleteClient_WithValidId_ShouldDeleteClient() {

        Long clientId = 1L;

        clientService.deleteClient(clientId);

        verify(clientRepository).deleteById(clientId);
    }

}