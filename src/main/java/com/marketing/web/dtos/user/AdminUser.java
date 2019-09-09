package com.marketing.web.dtos.user;

import com.marketing.web.dtos.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser extends DTO {

    private String id;

    private String username;

    private String name;

    private String email;

    private boolean status;
}
