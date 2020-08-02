package com.marketing.web.dtos.user.readable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser implements Serializable {

    private String id;

    private String username;

    private String name;

    private String email;

    private boolean status;
}
