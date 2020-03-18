package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "categories")
public class Category extends BaseModel {

    private UUID uuid;

    @NotBlank
    private String name;

    @NotBlank
    private String photoUrl;

    private boolean subCategory;

    private double commission;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent",cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id desc")
    private List<Category> childs;

    @JsonIgnore
    @OneToMany(mappedBy = "category",cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id desc")
    private List<Product> products;

    public List<Category> collectLeafChildren() {
        List<Category> results = new ArrayList<>();
        results.add(this);
        if (!childs.isEmpty()) {
            childs.forEach(child -> results.addAll(child.collectLeafChildren()));
        }
        return results;
    }

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
