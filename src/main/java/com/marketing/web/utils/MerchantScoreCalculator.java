package com.marketing.web.utils;

import com.marketing.web.models.Merchant;
import com.marketing.web.repositories.MerchantScoreRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MerchantScoreCalculator {

    private static MerchantScoreRepository sMerchantScoreRepository;

    private final MerchantScoreRepository merchantScoreRepository;

    public MerchantScoreCalculator(MerchantScoreRepository merchantScoreRepository) {
        this.merchantScoreRepository = merchantScoreRepository;
    }

    @PostConstruct
    private void initStaticDao () {
        sMerchantScoreRepository = this.merchantScoreRepository;
    }

    public static double getCalculation(Merchant merchant) {
        return sMerchantScoreRepository.merchantScoreAvg(merchant.getId()).orElse(0d);
    }
}
