package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.user.CustomerUser;
import com.marketing.web.dtos.user.MerchantUser;
import com.marketing.web.dtos.user.RegisterDTO;
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
    User registerDTOToUser(RegisterDTO registerDTO);

    @InheritInverseConfiguration
    Address registerDTOToAddress(RegisterDTO registerDTO);

    default MerchantUser userToMerchant(User user){
        MerchantUser merchantUser = new MerchantUser();
        merchantUser.setId(user.getId());
        merchantUser.setEmail(user.getEmail());
        merchantUser.setName(user.getName());
        merchantUser.setStatus(user.isStatus());
        merchantUser.setTaxNumber(user.getTaxNumber());
        merchantUser.setUserName(user.getUserName());
        merchantUser.setActiveStates(user.getActiveStates().stream().map(State::getTitle).collect(Collectors.toList()));
        return merchantUser;
    }

    default CustomerUser userToCustomer(User user){
        CustomerUser customerUser = new CustomerUser();
        customerUser.setId(user.getId());
        customerUser.setEmail(user.getEmail());
        customerUser.setName(user.getName());
        customerUser.setStatus(user.isStatus());
        customerUser.setTaxNumber(user.getTaxNumber());
        customerUser.setUserName(user.getUserName());
        customerUser.setAddress(user.getAddress());
        return customerUser;
    }

}
