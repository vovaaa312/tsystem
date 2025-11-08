package com.tsystem.model;

import com.tsystem.model.user.User;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_comments")
public class TicketComment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false) private Ticket ticket;
    @ManyToOne(optional = false) private User author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(nullable = false) private OffsetDateTime createdAt = OffsetDateTime.now();
}
