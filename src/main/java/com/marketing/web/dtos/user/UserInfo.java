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
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo extends DTO {

    private String id;

    private String username;

    private String name;

    private String email;

    private String role;

    private Address address;

    private List<String> activeStates;

    private UserInfo(Builder builder){
        this.id = builder.id;
        this.username = builder.username;
        this.name = builder.name;
        this.email = builder.email;
        this.role = builder.role;
        this.address = builder.address;
        this.activeStates = builder.activeStates;
    }

    public static class Builder {

        private String id;

        private String username;

        private String name;

        private String email;

        private String role;

        private Address address;

        private List<String> activeStates;

        public Builder(String username){
            this.username = username;
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder email(String email){
            this.email = email;
            return this;
        }

        public Builder role(String role){
            this.role = role;
            return this;
        }

        public Builder address(Address address){
            this.address = address;
            return this;
        }

        public Builder activeStates(List<String> activeStates){
            this.activeStates = activeStates;
            return this;
        }

        public UserInfo build() {
            UserInfo userInfo = new UserInfo(this);
            return userInfo;
        }

    }
}

