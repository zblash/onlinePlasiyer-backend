package com.marketing.web.dtos.user;

import com.marketing.web.models.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

    private Address address;

    private List<String> activeStates;
}
