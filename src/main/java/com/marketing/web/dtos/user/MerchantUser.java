package com.marketing.web.dtos.user;

import com.marketing.web.dtos.DTO;
import com.marketing.web.models.Address;
import com.marketing.web.models.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantUser extends DTO {

    private Long id;

    private String userName;

    private String name;

    private String email;

    private String taxNumber;

    private boolean status;

    private Address address;

    private List<String> activeStates;
}
