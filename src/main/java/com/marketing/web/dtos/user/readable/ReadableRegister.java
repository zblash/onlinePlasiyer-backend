package com.marketing.web.dtos.user.readable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadableRegister implements Serializable {

    private String id;

    private String username;

    private String name;

    private String email;

    private String taxNumber;

    private boolean status;
}
