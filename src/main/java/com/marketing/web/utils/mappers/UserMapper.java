package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.user.*;
import com.marketing.web.models.Address;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    @InheritInverseConfiguration
    User writableRegisterToUser(WritableRegister writableRegister);

    default ReadableRegister userToReadableRegister(User user){
        ReadableRegister readableRegister = new ReadableRegister();
        readableRegister.setId(user.getUuid().toString());
        readableRegister.setEmail(user.getEmail());
        readableRegister.setName(user.getName());
        readableRegister.setStatus(user.isStatus());
        readableRegister.setTaxNumber(user.getTaxNumber());
        readableRegister.setUsername(user.getUsername());
        return readableRegister;
    }

    @InheritInverseConfiguration
    Address registerDTOToAddress(WritableRegister writableRegister);

    default MerchantUser userToMerchant(User user){
        MerchantUser merchantUser = new MerchantUser();
        merchantUser.setId(user.getUuid().toString());
        merchantUser.setEmail(user.getEmail());
        merchantUser.setName(user.getName());
        merchantUser.setStatus(user.isStatus());
        merchantUser.setTaxNumber(user.getTaxNumber());
        merchantUser.setUsername(user.getUsername());
        merchantUser.setActiveStates(user.getActiveStates().stream().map(State::getTitle).collect(Collectors.toList()));
        return merchantUser;
    }

    default CustomerUser userToCustomer(User user){
        CustomerUser customerUser = new CustomerUser();
        customerUser.setId(user.getUuid().toString());
        customerUser.setEmail(user.getEmail());
        customerUser.setName(user.getName());
        customerUser.setStatus(user.isStatus());
        customerUser.setTaxNumber(user.getTaxNumber());
        customerUser.setUsername(user.getUsername());
        customerUser.setAddress(user.getAddress());
        return customerUser;
    }

}
