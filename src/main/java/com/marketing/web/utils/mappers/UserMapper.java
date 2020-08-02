package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.user.readable.*;
import com.marketing.web.dtos.user.register.BaseRegister;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.*;
import com.marketing.web.utils.MerchantScoreCalculator;

import java.util.stream.Collectors;

public final class UserMapper {

    public static User writableRegisterToUser(BaseRegister writableRegister) {
        if (writableRegister == null) {
            return null;
        } else {
            User user = new User();
            user.setUsername(writableRegister.getUsername());
            user.setName(writableRegister.getName());
            user.setPassword(writableRegister.getPassword());
            user.setEmail(writableRegister.getEmail());
            user.setPhoneNumber(writableRegister.getPhoneNumber());
            user.setAddressDetails(writableRegister.getDetails());
            return user;
        }
    }

    public static ReadableRegister userToReadableRegister(User user){
        if (user == null) {
            return null;
        } else {
            ReadableRegister readableRegister = new ReadableRegister();
            readableRegister.setId(user.getId().toString());
            readableRegister.setEmail(user.getEmail());
            readableRegister.setName(user.getName());
            readableRegister.setStatus(user.isStatus());
            readableRegister.setUsername(user.getUsername());
            return readableRegister;
        }
    }


    public static MerchantUser userToMerchant(Merchant merchant) {
        if (merchant == null) {
            return null;
        } else {
            MerchantUser merchantUser = new MerchantUser();
            merchantUser.setId(merchant.getId().toString());
            merchantUser.setEmail(merchant.getUser().getEmail());
            merchantUser.setName(merchant.getUser().getName());
            merchantUser.setStatus(merchant.getUser().isStatus());
            merchantUser.setTaxNumber(merchant.getTaxNumber());
            merchantUser.setUsername(merchant.getUser().getUsername());
            merchantUser.setActiveStates(merchant.getActiveStates().stream().map(State::getTitle).collect(Collectors.toList()));
            merchantUser.setCommission(merchant.getCommission());
            merchantUser.setAddress(UserMapper.addressToReadableAddress(merchant.getUser().getCity(),merchant.getUser().getState(),merchant.getUser().getAddressDetails()));
            return merchantUser;
        }
    }

    public static CommonMerchant merchantToCommonMerchant(Merchant merchant) {
        if (merchant == null) {
            return null;
        }
        CommonMerchant commonMerchant = new CommonMerchant();
        commonMerchant.setMerchantId(merchant.getId().toString());
        commonMerchant.setMerchantName(merchant.getUser().getName());
        commonMerchant.setMerchantScore(MerchantScoreCalculator.getCalculation(merchant));
        return commonMerchant;
    }

    public static CustomerUser userToCustomer(Customer customer) {
        if (customer == null) {
            return null;
        } else {
            CustomerUser customerUser = new CustomerUser();
            customerUser.setId(customer.getId().toString());
            customerUser.setEmail(customer.getUser().getEmail());
            customerUser.setName(customer.getUser().getName());
            customerUser.setStatus(customer.getUser().isStatus());
            customerUser.setTaxNumber(customer.getTaxNumber());
            customerUser.setUsername(customer.getUser().getUsername());
            customerUser.setAddress(UserMapper.addressToReadableAddress(customer.getUser().getCity(),customer.getUser().getState(),customer.getUser().getAddressDetails()));
            return customerUser;
        }
    }

    public static AdminUser userToAdmin(User user){
        if (user == null) {
            return null;
        } else {
            AdminUser adminUser = new AdminUser();
            adminUser.setId(user.getId().toString());
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
            readableAddress.setCityId(city.getId().toString());
            readableAddress.setCityName(city.getTitle());
            readableAddress.setStateId(state.getId().toString());
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
            userInfoBuilder.id(user.getId().toString());
            userInfoBuilder.email(user.getEmail());
            userInfoBuilder.name(user.getName());
            String role = user.getRole().getName().split("_")[1];
            userInfoBuilder.role(role);
            userInfoBuilder.address(UserMapper.addressToReadableAddress(user.getCity(),user.getState(),user.getAddressDetails()));
            return userInfoBuilder.build();
        }
    }

    public static ReadableUserInfo userToReadableUserInfo(Merchant merchant){
        if (merchant == null){
            return null;
        }else {
            ReadableUserInfo.Builder userInfoBuilder = new ReadableUserInfo.Builder(merchant.getUser().getUsername());
            userInfoBuilder.id(merchant.getId().toString());
            userInfoBuilder.email(merchant.getUser().getEmail());
            userInfoBuilder.name(merchant.getUser().getName());
            String role = merchant.getUser().getRole().getName().split("_")[1];
            userInfoBuilder.role(role);
            userInfoBuilder.address(UserMapper.addressToReadableAddress(merchant.getUser().getCity(),merchant.getUser().getState(),merchant.getUser().getAddressDetails()));
            userInfoBuilder.commission(merchant.getCommission());
            userInfoBuilder.activeStates(merchant.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
            return userInfoBuilder.build();
        }
    }

    public static ReadableUserInfo userToReadableUserInfo(Customer customer){
        if (customer == null){
            return null;
        }else {
            ReadableUserInfo.Builder userInfoBuilder = new ReadableUserInfo.Builder(customer.getUser().getUsername());
            userInfoBuilder.id(customer.getId().toString());
            userInfoBuilder.email(customer.getUser().getEmail());
            userInfoBuilder.name(customer.getUser().getName());
            String role = customer.getUser().getRole().getName().split("_")[1];
            userInfoBuilder.role(role);
            userInfoBuilder.address(UserMapper.addressToReadableAddress(customer.getUser().getCity(),customer.getUser().getState(),customer.getUser().getAddressDetails()));
            return userInfoBuilder.build();
        }
    }

}
