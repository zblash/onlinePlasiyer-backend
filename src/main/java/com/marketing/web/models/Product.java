package com.marketing.web.models;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "products")
public class Product extends BaseModel  {

    @NotBlank
    @Column(unique = true)
    private String name;

    @NotNull
    private double tax;

    private String photoUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    private Category category;

    private boolean status;

    private double commission;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("id desc")
    @EqualsAndHashCode.Exclude
    private Set<ProductSpecify> productSpecifies;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    @OrderBy("id desc")
    @EqualsAndHashCode.Exclude
    private Set<Barcode> barcodes;

    @ManyToMany
    private List<Merchant> merchants;

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

    public void addMerchant(Merchant merchant){
        if (merchants == null){
            merchants = new ArrayList<>();
        }
            if (!merchants.contains(merchant)){
                merchants.add(merchant);
            }
    }

    public void removeMerchant(Merchant merchant) {
        if (merchants != null){
            merchants.remove(merchant);
        }
    }
}
