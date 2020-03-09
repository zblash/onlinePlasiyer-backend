package com.marketing.web.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantUser implements Serializable {

    private String id;

    private String username;

    private String name;

    private String email;

    private String taxNumber;

    private boolean status;

    private ReadableAddress address;

    private List<String> activeStates;

    private double commission;
}
