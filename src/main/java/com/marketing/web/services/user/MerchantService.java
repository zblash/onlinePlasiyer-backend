package com.marketing.web.services.user;

import com.marketing.web.models.Merchant;
import com.marketing.web.models.State;
import com.marketing.web.models.User;

import java.util.List;

public interface MerchantService {

    List<Merchant> findAll();

    Merchant findById(String id);

    Merchant findByUser(User user);

    List<Merchant> findAllByUsers(List<User> users);

    List<Merchant> findAllByState(State state);

    Merchant create(Merchant merchant);

    Merchant update(String id, Merchant updatedMerchant);

    void delete(Merchant merchant);

    Merchant getLoggedInMerchant();

}
