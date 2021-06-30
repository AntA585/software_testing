package com.example.testing.customer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface CustomerRepository extends CrudRepository<Customer, UUID> {

    //@Query(value = "select id, name, phoneNumber from Customer where phoneNumber = :phoneNumber",nativeQuery = true)
    @Query(
            value = "select id, name, phoneNumber " +
                    "from customer where phoneNumber = phoneNumber",
            nativeQuery = true
    )
    Optional<Customer> selectCustomerByPhoneNumber(@Param("phoneNumber") String phoneNumber);

}
