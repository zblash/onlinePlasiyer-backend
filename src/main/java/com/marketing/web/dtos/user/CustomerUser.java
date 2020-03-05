package com.marketing.web.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUser implements Serializable {

    private String id;

    private String username;

    private String name;

    private String email;

    private String taxNumber;

    private boolean status;

    private ReadableAddress address;

}
