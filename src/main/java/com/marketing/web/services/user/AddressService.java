package com.marketing.web.services.user;

import com.marketing.web.models.Address;
import java.util.List;

public interface AddressService {

    List<Address> findAll();

    Address findById(Long id);

    Address findByUUID(String uuid);

    Address create(Address address);

    Address update(Address address,Address updatedAddress);

    void delete(Address Address);
}
