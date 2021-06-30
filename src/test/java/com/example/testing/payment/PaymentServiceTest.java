package com.example.testing.payment;

import com.example.testing.customer.Customer;
import com.example.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        underTest = new PaymentService(customerRepository,paymentRepository,cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {

        //Given
        UUID customerId = UUID.randomUUID();
        //...customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //...payment request
        PaymentRequest paymentRequest = new PaymentRequest(new Payment(
                null,
                null,
                new BigDecimal("100.00"),
                Currency.USD,
                "card123xx",
                "Donation"));

        //...Card is charged successfully
        given((cardPaymentCharger.chargeCard(

                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        ))).willReturn(new CardPaymentCharge(true));

        //When
        underTest.chargeCard(customerId,paymentRequest);

        //Then
        ArgumentCaptor<Payment> paymentRequestArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentRequestArgumentCaptor.capture());

        Payment paymentArgumentCaptorValue = paymentRequestArgumentCaptor.getValue();
        assertThat(paymentArgumentCaptorValue).isEqualToIgnoringGivenFields(paymentRequest.getPayment(),"customerId");

        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);
    }
}