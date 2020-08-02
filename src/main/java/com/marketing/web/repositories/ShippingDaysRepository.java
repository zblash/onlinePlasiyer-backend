package com.marketing.web.repositories;

import com.marketing.web.models.Merchant;
import com.marketing.web.models.ShippingDays;
import com.marketing.web.models.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShippingDaysRepository extends JpaRepository<ShippingDays, UUID>, JpaSpecificationExecutor<ShippingDays> {

    List<ShippingDays> findAllByMerchant(Merchant merchant);

    Optional<ShippingDays> findByMerchantAndState(Merchant merchant, State state);

    @Query("SELECT CASE WHEN COUNT(sd) > 0 THEN 'true' ELSE 'false' END FROM ShippingDays sd WHERE sd.merchant = ?1 AND sd.state = ?2")
    Boolean hasDaysInStateMerchant(Merchant merchant, State state);

    Optional<ShippingDays> findByIdAndMerchant(UUID id, Merchant merchant);
}
