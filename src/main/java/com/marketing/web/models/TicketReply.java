package com.marketing.web.models;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "ticketreplies")
public class TicketReply {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

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