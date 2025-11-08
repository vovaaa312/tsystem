package com.tsystem.model;

import com.tsystem.model.enums.TicketPriority;
import com.tsystem.model.enums.TicketState;
import com.tsystem.model.user.User;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_history")
public class TicketHistory {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false) private Ticket ticket;
    @ManyToOne(optional = false) private User changedBy;

    @Enumerated(EnumType.STRING) private TicketState oldState;
    @Enumerated(EnumType.STRING) private TicketState newState;

    @Enumerated(EnumType.STRING) private TicketPriority oldPriority;
    @Enumerated(EnumType.STRING) private TicketPriority newPriority;

    @ManyToOne private User oldAssignee;
    @ManyToOne private User newAssignee;

    @Column(nullable = false) private OffsetDateTime changedAt = OffsetDateTime.now();
}
