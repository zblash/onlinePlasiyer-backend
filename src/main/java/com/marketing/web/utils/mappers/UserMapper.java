package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.user.*;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.City;
import com.marketing.web.models.Role;
import com.marketing.web.models.State;
import com.marketing.web.models.User;

import java.util.stream.Collectors;

public final class UserMapper {

    public static User writableRegisterToUser(WritableRegister writableRegister) {
        if (writableRegister == null) {
            return null;
        } else {
            User user = new User();
            user.setUsername(writableRegister.getUsername());
            user.setName(writableRegister.getName());
            user.setPassword(writableRegister.getPassword());
            user.setEmail(writableRegister.getEmail());
            user.setTaxNumber(writableRegister.getTaxNumber());
            return user;
        }
    }

    public static ReadableRegister userToReadableRegister(User user){
        if (user == null) {
            return null;
        } else {
            ReadableRegister readableRegister = new ReadableRegister();
            readableRegister.setId(user.getUuid().toString());
            readableRegister.setEmail(user.getEmail());
            readableRegister.setName(user.getName());
            readableRegister.setStatus(user.isStatus());
            readableRegister.setTaxNumber(user.getTaxNumber());
            readableRegister.setUsername(user.getUsername());
            return readableRegister;
        }
    }


    public static MerchantUser userToMerchant(User user) {
        if (user == null) {
            return null;
        } else {
            MerchantUser merchantUser = new MerchantUser();
            merchantUser.setId(user.getUuid().toString());
            merchantUser.setEmail(user.getEmail());
            merchantUser.setName(user.getName());
            merchantUser.setStatus(user.isStatus());
            merchantUser.setTaxNumber(user.getTaxNumber());
            merchantUser.setUsername(user.getUsername());
            merchantUser.setActiveStates(user.getActiveStates().stream().map(State::getTitle).collect(Collectors.toList()));
            merchantUser.setCommission(user.getCommission());
            merchantUser.setAddress(UserMapper.addressToReadableAddress(user.getCity(),user.getState(),user.getAddressDetails()));
            return merchantUser;
        }
    }

    public static CustomerUser userToCustomer(User user) {
        if (user == null) {
            return null;
        } else {
            CustomerUser customerUser = new CustomerUser();
            customerUser.setId(user.getUuid().toString());
            customerUser.setEmail(user.getEmail());
            customerUser.setName(user.getName());
            customerUser.setStatus(user.isStatus());
            customerUser.setTaxNumber(user.getTaxNumber());
            customerUser.setUsername(user.getUsername());
            customerUser.setAddress(UserMapper.addressToReadableAddress(user.getCity(),user.getState(),user.getAddressDetails()));
            return customerUser;
        }
    }

    public static AdminUser userToAdmin(User user){
        if (user == null) {
            return null;
        } else {
            AdminUser adminUser = new AdminUser();
            adminUser.setId(user.getUuid().toString());
            adminUser.setEmail(user.getEmail());
            adminUser.setName(user.getName());
            adminUser.setStatus(user.isStatus());
            adminUser.setUsername(user.getUsername());
            return adminUser;
        }
    }

    public static RoleType roleToRoleType(Role role){
        if (role == null) {
            return null;
        } else {
            return RoleType.fromValue(role.getName().split("_")[1]);
        }
    }

    public static ReadableAddress addressToReadableAddress(City city, State state, String details){
        if (city == null || state == null || details.isEmpty()){
            return null;
        } else {
            ReadableAddress readableAddress = new ReadableAddress();
            readableAddress.setCityId(city.getUuid().toString());
            readableAddress.setCityName(city.getTitle());
            readableAddress.setStateId(state.getUuid().toString());
            readableAddress.setStateName(state.getTitle());
            readableAddress.setDetails(details);
            return readableAddress;
        }
    }

    public static ReadableUserInfo userToReadableUserInfo(User user){
        if (user == null){
            return null;
        }else {
            ReadableUserInfo.Builder userInfoBuilder = new ReadableUserInfo.Builder(user.getUsername());
            userInfoBuilder.id(user.getUuid().toString());
            userInfoBuilder.email(user.getEmail());
            userInfoBuilder.name(user.getName());
            String role = user.getRole().getName().split("_")[1];
            userInfoBuilder.role(role);
            userInfoBuilder.address(UserMapper.addressToReadableAddress(user.getCity(),user.getState(),user.getAddressDetails()));
            userInfoBuilder.activeStates(user.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
            userInfoBuilder.commission(user.getCommission());
            return userInfoBuilder.build();
        }
    }

}
