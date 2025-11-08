package com.tsystem.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_attachments")
public class TicketAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false) private Ticket ticket;

    @Column(nullable = false) private String filename;
    private String contentType;
    private Long size;
    private String url;

    @Column(nullable = false) private OffsetDateTime uploadedAt = OffsetDateTime.now();
}
