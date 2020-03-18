package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "users")
public class User extends BaseModel {

    private UUID uuid;

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    @Size(min = 5, max = 90)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String taxNumber;

    private double commission;

    private boolean status;

    private String activationToken;

    private String passwordResetToken;

    private Date resetTokenExpireTime;

    @ManyToOne
    @JoinColumn(name = "role_id",referencedColumnName = "id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "city_id",referencedColumnName = "id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "state_id",referencedColumnName = "id")
    private State state;

    private String addressDetails;


    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<State> activeStates;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL,mappedBy = "user")
    @JoinColumn(name = "cart_id",referencedColumnName = "id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Cart cart;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
