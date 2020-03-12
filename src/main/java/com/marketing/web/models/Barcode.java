package com.marketing.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "barcodes")
public class Barcode extends BaseModel {

    private UUID uuid;

    @Column(unique = true)
    private String barcodeNo;

    @ManyToOne
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    private Product product;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }


}
