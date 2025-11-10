package com.tsystem.model;

import com.tsystem.model.enums.TicketPriority;
import com.tsystem.model.enums.TicketState;
import com.tsystem.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "ticket_history",
        indexes = {
                @Index(name = "idx_ticket_history_ticket", columnList = "ticket_id"),
                @Index(name = "idx_ticket_history_changed_at", columnList = "changed_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id", nullable = false)
    private User changedBy;

    @Enumerated(EnumType.STRING)
    private TicketState oldState;

    @Enumerated(EnumType.STRING)
    private TicketState newState;

    @Enumerated(EnumType.STRING)
    private TicketPriority oldPriority;

    @Enumerated(EnumType.STRING)
    private TicketPriority newPriority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_assignee_id")
    private User oldAssignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_assignee_id")
    private User newAssignee;

    @Column(name = "changed_at", nullable = false)
    @Builder.Default
    private OffsetDateTime changedAt = OffsetDateTime.now();
}
