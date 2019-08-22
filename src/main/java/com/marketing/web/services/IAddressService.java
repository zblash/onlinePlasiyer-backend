package com.marketing.web.services;

import com.marketing.web.models.Address;
import java.util.List;

public interface IAddressService {

    List<Address> findAll();

    Address findById(Long id);

    Address create(Address address);

    Address update(Address address,Address updatedAddress);

    void delete(Address Address);
}
