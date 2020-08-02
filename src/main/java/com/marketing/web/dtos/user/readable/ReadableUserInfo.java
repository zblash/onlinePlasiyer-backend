package com.marketing.web.dtos.user.readable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadableUserInfo implements Serializable {

    private String id;

    private String username;

    private String name;

    private String email;

    private String role;

    private ReadableAddress address;

    private double commission;

    private List<ReadableState> activeStates;

    private ReadableUserInfo(Builder builder){
        this.id = builder.id;
        this.username = builder.username;
        this.name = builder.name;
        this.email = builder.email;
        this.role = builder.role;
        this.address = builder.address;
        this.activeStates = builder.activeStates;
        this.commission = builder.commission;
    }

    public static class Builder {

        private String id;

        private String username;

        private String name;

        private String email;

        private String role;

        private ReadableAddress address;

        private double commission;

        private List<ReadableState> activeStates;

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

        public Builder address(ReadableAddress address){
            this.address = address;
            return this;
        }

        public Builder commission(double commission) {
            this.commission = commission;
            return this;
        }

        public Builder activeStates(List<ReadableState> activeStates){
            this.activeStates = activeStates;
            return this;
        }

        public ReadableUserInfo build() {
            ReadableUserInfo userInfo = new ReadableUserInfo(this);
            return userInfo;
        }

    }
}

