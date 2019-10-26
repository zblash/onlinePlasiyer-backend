package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @NotBlank
    @Size(min = 3,max = 20)
    private String username;

    @NotBlank
    @Size(min = 3,max = 20)
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

    private boolean status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id",referencedColumnName = "id")
    private Role role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id",referencedColumnName = "id")
    private Address address;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<State> activeStates;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL,mappedBy = "user")
    @JoinColumn(name = "cart_id",referencedColumnName = "id")
    private Cart cart;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
