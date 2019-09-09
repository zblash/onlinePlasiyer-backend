package com.marketing.web.dtos.user;

import com.marketing.web.dtos.DTO;
import com.marketing.web.models.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableLogin extends DTO {

    private String username;

    private String name;

    private String email;

    private String role;

    private String token;

    private Address address;

    private List<ReadableState> activeStates;

    private ReadableLogin(LoginDTOBuilder builder){
        this.username = builder.username;
        this.name = builder.name;
        this.email = builder.email;
        this.role = builder.role;
        this.token = builder.token;
        this.address = builder.address;
        this.activeStates = builder.activeStates;
    }

    public static class LoginDTOBuilder {

        private String username;

        private String name;

        private String email;

        private String role;

        private String token;

        private Address address;

        private List<ReadableState> activeStates;

        public LoginDTOBuilder(String token){
            this.token = token;
        }

        public LoginDTOBuilder userName(String userName){
            this.username = userName;
            return this;
        }

        public LoginDTOBuilder name(String name){
            this.name = name;
            return this;
        }

        public LoginDTOBuilder email(String email){
            this.email = email;
            return this;
        }

        public LoginDTOBuilder role(String role){
            this.role = role;
            return this;
        }

        public LoginDTOBuilder address(Address address){
            this.address = address;
            return this;
        }

        public LoginDTOBuilder activeStates(List<ReadableState> activeStates){
            this.activeStates = activeStates;
            return this;
        }

        public ReadableLogin build() {
            ReadableLogin readableLogin = new ReadableLogin(this);
            return readableLogin;
        }

    }
}
