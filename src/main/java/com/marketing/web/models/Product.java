package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @NotBlank
    @Size(min = 3,max = 20)
    private String name;

    @NotBlank
    @Size(min = 10,max = 100)
    @Column(unique = true)
    private String barcode;

    @NotNull
    private double tax;

    private String photoUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    private Category category;

    private boolean status;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private Set<ProductSpecify> productSpecifies;

    public void addProductSpecify(ProductSpecify productSpecify){
        if (productSpecifies == null){
            productSpecifies = new HashSet<>();
        }
        productSpecifies.add(productSpecify);
    }

    public void removeProductSpecify(ProductSpecify productSpecify){
        if (productSpecifies != null){
            productSpecifies.remove(productSpecify);
        }

    }

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
