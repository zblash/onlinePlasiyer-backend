package com.marketing.web.services.user;


import com.marketing.web.models.Customer;
import com.marketing.web.models.State;
import com.marketing.web.models.User;

import java.util.List;

public interface CustomerService {

    List<Customer> findAll();

    Customer findById(String id);

    List<Customer> findAllByUsers(List<User> users);

    List<Customer> findAllByStatesAndStatus(List<State> activeStates, boolean status);

    Customer findByUser(User user);

    Customer create(Customer customer);

    Customer update(String id, Customer updatedCustomer);

    void delete(Customer customer);

    Customer getLoggedInCustomer();
}
