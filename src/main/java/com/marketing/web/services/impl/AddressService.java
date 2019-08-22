package com.marketing.web.services.impl;

import com.marketing.web.models.Address;
import com.marketing.web.repositories.AddressRepository;
import com.marketing.web.services.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService implements IAddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    @Override
    public Address findById(Long id) {
        return addressRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public Address create(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public Address update(Address address, Address updatedAddress) {
        address.setCity(updatedAddress.getCity());
        address.setState(updatedAddress.getState());
        address.setDetails(updatedAddress.getDetails());
        return addressRepository.save(address);
    }

    @Override
    public void delete(Address address) {
        addressRepository.delete(address);
    }
}
