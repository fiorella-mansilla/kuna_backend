package com.kuna_backend;

import com.kuna_backend.builders.PaymentTestDataBuilder;
import com.kuna_backend.dtos.checkout.CheckoutItemDto;
import com.kuna_backend.enums.PaymentStatus;
import com.kuna_backend.models.Cart;
import com.kuna_backend.models.Client;
import com.kuna_backend.models.Payment;
import com.kuna_backend.repositories.CartRepository;
import com.kuna_backend.repositories.PaymentRepository;
import com.kuna_backend.services.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    public Session session;

    @Mock
    public Client client;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Stripe.apiKey = "sk_test_51MaxUkEyNC1M1bEEpJcQrzYAcde8ST7cCT4NqohGmnyPz6OWsWQxWFbcUFbX2pytE5JjVeopRcBeh8RxPObdsmcz009C6OlGHh";
        paymentService.baseURL = "http://localhost:8080/";
    }

    @Test
    public void createPriceData() {

        CheckoutItemDto checkoutItemDto = PaymentTestDataBuilder.buildCheckoutItemDto();

        SessionCreateParams.LineItem.PriceData result = paymentService.createPriceData(checkoutItemDto);

        assertEquals("usd", result.getCurrency());
        assertEquals(1000L, result.getUnitAmount());
        assertEquals("Product 1", result.getProductData().getName());
    }

    @Test
    public void createSessionLineItem() {

        CheckoutItemDto checkoutItemDto = PaymentTestDataBuilder.buildCheckoutItemDto();

        SessionCreateParams.LineItem result = paymentService.createSessionLineItem(checkoutItemDto);

        assertEquals("usd", result.getPriceData().getCurrency());
        assertEquals(1000L, result.getPriceData().getUnitAmount());
        assertEquals(2, result.getQuantity());
    }

    @Test
    public void createCheckoutSession() throws StripeException {

        String successURL = paymentService.baseURL + "payment/success";
        String failureURL = paymentService.baseURL + "payment/failed";
        List<Cart> cartList = new ArrayList<>();

        when(cartRepository.findAllByClientOrderByCreatedAtDesc(client)).thenReturn(cartList);

        SessionCreateParams.Builder paramsBuilder = mock(SessionCreateParams.Builder.class);
        SessionCreateParams params = mock(SessionCreateParams.class);
        Session expectedSession = mock(Session.class);

        try (MockedStatic<SessionCreateParams> sessionBuilderMock = mockStatic(SessionCreateParams.class);
             MockedStatic<Session> sessionMock = mockStatic(Session.class);
             MockedConstruction<SessionCreateParams.LineItem> lineItemConstruction = mockConstruction(SessionCreateParams.LineItem.class)) {

            // Stub the builder methods
            when(SessionCreateParams.builder()).thenReturn(paramsBuilder);
            when(paramsBuilder.addPaymentMethodType(any())).thenReturn(paramsBuilder);
            when(paramsBuilder.setMode(any())).thenReturn(paramsBuilder);
            when(paramsBuilder.setCancelUrl(eq(failureURL))).thenReturn(paramsBuilder);
            when(paramsBuilder.setSuccessUrl(eq(successURL))).thenReturn(paramsBuilder);
            when(paramsBuilder.addAllLineItem(any())).thenReturn(paramsBuilder);
            when(paramsBuilder.build()).thenReturn(params);

            // Stub the Session.create method
            sessionMock.when(() -> Session.create(params)).thenReturn(expectedSession);

            Session session = paymentService.createSession(client);

            assertEquals(expectedSession, session);
            verify(paramsBuilder).addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD);
            verify(paramsBuilder).setMode(SessionCreateParams.Mode.PAYMENT);
            verify(paramsBuilder).setCancelUrl(failureURL);
            verify(paramsBuilder).addAllLineItem(any());
            verify(paramsBuilder).setSuccessUrl(successURL);
            verify(paramsBuilder).build();
            verify(cartRepository).findAllByClientOrderByCreatedAtDesc(client);

            sessionMock.verify(() -> Session.create(params));
        }
    }

    @Test
    public void getDtoFromCart() {

        Cart cart = PaymentTestDataBuilder.buildCart();
        String expectedProductName = cart.getProductVariation().getProduct().getName();
        int expectedCartQuantity = cart.getQuantity();
        double expectedProductPrice = cart.getProductVariation().getProduct().getPrice();
        Long expectedId = cart.getProductVariation().getId();

        CheckoutItemDto checkoutItemDto = PaymentService.getDtoFromCart(cart);
        String productName = checkoutItemDto.getProductName();
        int cartQuantity = checkoutItemDto.getQuantity();
        double productPrice = checkoutItemDto.getPrice();
        Long id = checkoutItemDto.getProductVariationId();

        assertEquals(expectedProductName, productName);
        assertEquals(expectedCartQuantity, cartQuantity);
        assertEquals(expectedProductPrice, productPrice);
        assertEquals(expectedId, id);
    }

    @Test
    public void savePaymentFromCheckoutSession() throws StripeException {

        String stripeToken = "cs_test_a14LmWmbRX68zuH7z45k2cosRfWoq5sTE1srkhuekLlzhHzgI1Hu6Dqh9M";

        // Mock the static method Session.retrieve
        try (MockedStatic<Session> sessionMock = mockStatic(Session.class)) {
            sessionMock.when(() -> Session.retrieve(stripeToken)).thenReturn(session);
            when(session.getAmountTotal()).thenReturn(1000L);
            when(session.getCurrency()).thenReturn("usd");
            when(session.getId()).thenReturn(stripeToken);
            when(session.getPaymentStatus()).thenReturn("PAID");
            when(session.getCreated()).thenReturn(System.currentTimeMillis() / 1000L);

            Client clientMock = mock(Client.class);

            paymentService.savePaymentFromSession(stripeToken, clientMock);

            verify(paymentRepository).save(argThat(payment ->
                    payment.getAmount() == 10.0f &&
                            payment.getCurrency().equals("USD") &&
                            payment.getStripeToken().equals(stripeToken) &&
                            payment.getPaymentStatus().equals(PaymentStatus.PAID) &&
                            payment.getProvider().equals("Stripe") &&
                            payment.getClient() == clientMock
            ));
        }
    }

    @Test
    public void getAllPayments() {

        List<Payment> expectedPaymentList = new ArrayList<>();
        when(paymentRepository.findAll()).thenReturn(expectedPaymentList);

        List<Payment> paymentList = paymentService.getAllPayments();

        assertEquals(expectedPaymentList, paymentList);
        verify(paymentRepository).findAll();
    }

    @Test
    public void getPaymentById() {

        String stripeToken = "test_token";
        Payment expectedPayment = new Payment();
        when(paymentRepository.findPaymentByStripeToken(stripeToken)).thenReturn(expectedPayment);

        Payment payment = paymentService.getPaymentById(stripeToken);

        assertEquals(expectedPayment, payment);
        verify(paymentRepository).findPaymentByStripeToken(stripeToken);
    }

}
