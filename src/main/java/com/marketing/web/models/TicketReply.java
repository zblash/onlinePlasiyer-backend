package com.marketing.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "ticketreplies")
public class TicketReply extends BaseModel {

    private UUID uuid;

    @NotBlank
    private String message;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id",referencedColumnName = "id")
    private Ticket ticket;

    @Temporal(TemporalType.DATE)
    private Date addedTime;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
