package com.marketing.web.models;

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
@NoArgsConstructor
@AllArgsConstructor
@Data
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
    @OrderBy("id desc")
    @EqualsAndHashCode.Exclude
    private Set<ProductSpecify> productSpecifies;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    @OrderBy("id desc")
    @EqualsAndHashCode.Exclude
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
