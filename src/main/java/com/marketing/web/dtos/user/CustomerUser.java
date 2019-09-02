package com.marketing.web.dtos.user;

import com.marketing.web.dtos.DTO;
import com.marketing.web.models.Address;
import com.marketing.web.models.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUser extends DTO {

    private Long id;

    private String userName;

    private String name;

    private String email;

    private String taxNumber;

    private boolean status;

    private Address address;

}
