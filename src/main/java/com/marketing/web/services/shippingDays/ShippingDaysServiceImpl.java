package com.marketing.web.services.shippingDays;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.ShippingDays;
import com.marketing.web.models.State;
import com.marketing.web.repositories.ShippingDaysRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ShippingDaysServiceImpl implements ShippingDaysService {

    private Logger logger = LoggerFactory.getLogger(ShippingDaysServiceImpl.class);

    private final ShippingDaysRepository shippingDaysRepository;

    public ShippingDaysServiceImpl(ShippingDaysRepository shippingDaysRepository) {
        this.shippingDaysRepository = shippingDaysRepository;
    }

    @Override
    public List<ShippingDays> findAll() {
        return shippingDaysRepository.findAll();
    }

    @Override
    public List<ShippingDays> findAllByMerchant(Merchant merchant) {
        return shippingDaysRepository.findAllByMerchant(merchant);
    }

    @Override
    public ShippingDays findByMerchantAndState(Merchant merchant, State state) {
        return shippingDaysRepository.findByMerchantAndState(merchant, state).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"shipping.days", ""));
    }

    @Override
    public boolean hasShippingDays(Merchant merchant, State state) {
        return shippingDaysRepository.hasDaysInStateMerchant(merchant, state);
    }

    @Override
    public ShippingDays findByMerchantAndId(Merchant merchant, String id) {
        return shippingDaysRepository.findByIdAndMerchant(UUID.fromString(id), merchant).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"shipping.days", id));
    }

    @Override
    public ShippingDays findById(String id) {
        return shippingDaysRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"resources.notfound.shipping.days", id));
    }

    @Override
    public ShippingDays create(ShippingDays shippingDays) {
        return shippingDaysRepository.save(shippingDays);
    }

    @Override
    public ShippingDays update(String id, ShippingDays updatedShippingDays) {
        ShippingDays shippingDays = findById(id);
        shippingDays.setDays(updatedShippingDays.getDays());
        shippingDays.setState(updatedShippingDays.getState());
        shippingDays.setMerchant(updatedShippingDays.getMerchant());
        return shippingDaysRepository.save(shippingDays);
    }

    @Override
    public void delete(ShippingDays shippingDays) {
        shippingDaysRepository.delete(shippingDays);
    }
}
