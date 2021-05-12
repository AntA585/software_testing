package com.example.testing.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        //1. check whether the phoneNumber is taken
        //2. if it is taken, let's check if it belongs to the same customer
        //2.1 If yes, return
        //2.2 throw an exception
        //3. save customer
        String phoneNumber = request.getCustomer().getPhoneNumber();
        Optional<Customer> customerOptional = customerRepository.selectCustomerByPhoneNumber(phoneNumber);

        if (customerOptional.isPresent()) {

            Customer customer = customerOptional.get();
            if (customer.getName().equals(request.getCustomer().getName())) {
                return;
            }
            throw new IllegalStateException(String.format("This phone number [%s] already exists", phoneNumber));
        } else {

            if (request.getCustomer().getId() == null) {
                request.getCustomer().setId(UUID.randomUUID());
            }
        }
        customerRepository.save(request.getCustomer());

    }
}

