package com.marketing.web.services.user;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.configs.security.CustomPrincipal;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Customer;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.repositories.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findById(String id) {
        return customerRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"user", id));
    }

    @Override
    public List<Customer> findAllByUsers(List<User> users) {
       return customerRepository.findAllByUserIn(users);
    }

    @Override
    public List<Customer> findAllByStatesAndStatus(List<State> activeStates, boolean status) {
       return customerRepository.findAllByUserStateInAndUserStatus(activeStates, status);
    }

    @Override
    public Customer findByUser(User user) {
        return customerRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"merchant",user.getId().toString()));
    }

    @Override
    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(String id, Customer updatedCustomer) {
        Customer customer = findById(id);
        customer.setTaxNumber(updatedCustomer.getTaxNumber());
        customer.setUser(updatedCustomer.getUser());
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }

    @Override
    public Customer getLoggedInCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((CustomPrincipal) auth.getPrincipal()).getCustomer();
    }
}
