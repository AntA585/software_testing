package com.example.testing.payment;

import com.example.testing.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(CustomerRepository customerRepository,PaymentRepository paymentRepository,CardPaymentCharger cardPaymentCharger){
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    void chargeCard(UUID customerId, PaymentRequest paymentRequest){
        

    }
}
