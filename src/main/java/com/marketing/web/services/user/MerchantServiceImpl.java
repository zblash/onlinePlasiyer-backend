package com.marketing.web.services.user;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.configs.security.CustomPrincipal;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.repositories.MerchantRepository;
import com.marketing.web.repositories.MerchantScoreRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantServiceImpl(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public List<Merchant> findAll() {
        return merchantRepository.findAll();
    }

    @Override
    public Merchant findById(String id) {
        return merchantRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"merchant",id));
    }

    @Override
    public Merchant findByUser(User user) {
        return merchantRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"merchant",user.getId().toString()));
    }

    @Override
    public List<Merchant> findAllByUsers(List<User> users) {
       return merchantRepository.findAllByUserIn(users);
    }

    @Override
    public List<Merchant> findAllByState(State state) {
       return merchantRepository.findAllByActiveStatesContains(state);
    }

    @Override
    public Merchant create(Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    @Override
    public Merchant update(String id, Merchant updatedMerchant) {
        Merchant merchant = findById(id);
        merchant.setCommission(updatedMerchant.getCommission());
        merchant.setTaxNumber(updatedMerchant.getTaxNumber());
        merchant.setActiveStates(updatedMerchant.getActiveStates());
        merchant.setUser(updatedMerchant.getUser());
        return merchantRepository.save(merchant);
    }

    @Override
    public void delete(Merchant merchant) {
        merchantRepository.delete(merchant);
    }

    @Override
    public Merchant getLoggedInMerchant() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((CustomPrincipal) auth.getPrincipal()).getMerchant();
    }

}
