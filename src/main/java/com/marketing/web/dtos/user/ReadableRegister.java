package com.marketing.web.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadableRegister {

    private Long id;

    private String userName;

    private String name;

    private String email;

    private String taxNumber;

    private boolean status;
}
