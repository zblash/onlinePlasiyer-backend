package com.marketing.web.services.user;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Address;
import com.marketing.web.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    @Override
    public Address findById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address not found with id: "+ id));
    }

    @Override
    public Address findByUUID(String uuid) {
        return addressRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("Address not found with id: "+ uuid));
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
