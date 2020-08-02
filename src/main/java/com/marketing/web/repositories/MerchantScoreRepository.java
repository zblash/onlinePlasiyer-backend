package com.marketing.web.repositories;

import com.marketing.web.models.MerchantScore;
import com.marketing.web.models.MerchantScoreComposite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface MerchantScoreRepository extends JpaRepository<MerchantScore, MerchantScoreComposite> {

    @Query(value = "SELECT avg(score) FROM merchant_scores WHERE merchant_id = ?1", nativeQuery = true)
    Optional<Double> merchantScoreAvg(UUID merchantId);

}
