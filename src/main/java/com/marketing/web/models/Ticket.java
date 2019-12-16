package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "tickets")
public class Ticket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @NotBlank
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Temporal(TemporalType.DATE)
    private Date addedTime;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "ticket",cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("id desc")
    private List<TicketReply> ticketReplies;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
