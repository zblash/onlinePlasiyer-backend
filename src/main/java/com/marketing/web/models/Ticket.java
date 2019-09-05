package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.enums.TicketStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "tickets")
public class Ticket extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private TicketStatus status;

    @Temporal(TemporalType.DATE)
    private Date addedTime;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "ticket",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TicketReply> ticketReplies;
}
