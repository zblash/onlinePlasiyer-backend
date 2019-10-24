package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product implements Serializable  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @NotBlank
    @Size(min = 3,max = 20)
    @Column(unique = true)
    private String name;

    @NotNull
    private double tax;

    private String photoUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    private Category category;

    private boolean status;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<ProductSpecify> productSpecifies;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private Set<Barcode> barcodes;

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

    public void addBarcode(Barcode barcode){
        if (barcodes == null){
            barcodes = new HashSet<>();
        }
        barcodes.add(barcode);
    }

    public void removeBarcode(Barcode barcode){
        if (barcodes != null){
            barcodes.remove(barcode);
        }

    }

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
