package com.example.testing.payment;

import com.example.testing.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD, Currency.GBP);
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;


    @Autowired
    public PaymentService(CustomerRepository customerRepository, PaymentRepository paymentRepository, CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    void chargeCard(UUID customerId, PaymentRequest paymentRequest) {
        //1. Does customer exist, if not throw
        boolean isCustomerFound = customerRepository.existsById(customerId);
        if (!isCustomerFound) {
            throw new IllegalStateException(String.format("This customer does not exist with this id [%s]", customerId));
        }

        //2. Do we support the currency, if not throw
        boolean acceptedCurrency = ACCEPTED_CURRENCIES.stream()
                .anyMatch(c -> c.equals(paymentRequest.getPayment().getCurrency()));

        if (!acceptedCurrency) {
            String message = String.format("Currency[%s] not suppoerted", paymentRequest.getPayment().getCurrency());
            throw new IllegalStateException(message);
        }

        //3. Charge card

        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        //4. If not debited throw
        if (!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException(String.format("Card was not debited for customer %s", customerId));
        }
        //5. Insert payment
        paymentRequest.getPayment().setCustomerId(customerId);

        paymentRepository.save(paymentRequest.getPayment());

        //6. TODO: send sms


    }
}
