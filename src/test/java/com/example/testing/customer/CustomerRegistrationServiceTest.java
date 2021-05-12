package com.example.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() {
        //Given a phone number and a customer
        String phoneNumber = "2168320607";
        Customer customer = new Customer(UUID.randomUUID(), "Harper", phoneNumber);
        //...a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        //When
        underTest.registerNewCustomer(request);
        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualTo(customer);
    }

    @Test
    void itShouldNotSaveCustomerWhenACustomerExists() {
        //Given
        String phoneNumber = "2168320607";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Harper", phoneNumber);
        //...a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        //when an existing customer is returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));
        //When
        underTest.registerNewCustomer(request);
        //Then
        then(customerRepository).should(never()).save(any());
        then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
        then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldThrowAnErrorIfThePhoneNumberAlreadyExists() {
        //Given
        String phoneNumber = "2168320607";
        UUID id = UUID.randomUUID();
        Customer customer1 = new Customer(id, "Harper", phoneNumber);
        Customer customer2 = new Customer(id, "Jazatavia", phoneNumber);
        //...a request is submitted with a given phone number
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer1);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer2));
        //Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class).
                hasMessageContaining(String.format("This phone number [%s] already exists", phoneNumber));

        //Finally
        then(customerRepository).should(never()).save(any(Customer.class));
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        //Given a phone number and a customer
        String phoneNumber = "2168320607";
        Customer customer = new Customer(null, "Harper", phoneNumber);
        //...a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        //When
        underTest.registerNewCustomer(request);
        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }
}