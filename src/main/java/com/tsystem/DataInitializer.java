package com.tsystem;

import com.tsystem.model.Project;
import com.tsystem.model.Ticket;
import com.tsystem.model.TicketComment;
import com.tsystem.model.enums.TicketPriority;
import com.tsystem.model.enums.TicketState;
import com.tsystem.model.enums.TicketType;
import com.tsystem.model.user.SystemRole;
import com.tsystem.model.user.User;
import com.tsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository users;
    private final ProjectRepository projects;
    private final TicketRepository tickets;
    private final TicketCommentRepository comments;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {

        // ---------- USERS ----------
        User admin = createIfMissing(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "admin",
                "Admin",
                "System",
                "admin@mail.com",
                "admin",
                SystemRole.SYSTEM_ADMIN
        );
        User test1 = createIfMissing(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "test",
                "Test",
                "User1",
                "test@mail.com",
                "test",
                SystemRole.SYSTEM_USER
        );
        User test2 = createIfMissing(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "test2",
                "Test",
                "User2",
                "test2@mail.com",
                "test2",
                SystemRole.SYSTEM_USER
        );

        // ---------- PROJECT ----------
        UUID projectId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        Project project = projects.findById(projectId).orElseGet(() -> {
            Project p = Project.builder()
                    .id(projectId)
                    .name("Demo Ticket System")
                    .description("Project for testing ELK & ticket analytics")
                    .user(admin)
                    .createdAt(OffsetDateTime.now())
                    .build();
            return projects.save(p);
        });

        // ---------- TICKETS ----------
        Ticket t1 = createTicket(project, admin, test1,
                "API endpoint failure",
                "Service returns 500 when updating ticket",
                TicketPriority.high,
                TicketState.open);
        Ticket t2 = createTicket(project, admin, test1,
                "Login form validation",
                "Error message not localized",
                TicketPriority.med,
                TicketState.in_progress);
        Ticket t3 = createTicket(project, admin, test2,
                "Docker build error",
                "Frontend container fails to build on Ubuntu",
                TicketPriority.high,
                TicketState.open);
        Ticket t4 = createTicket(project, admin, test2,
                "Elasticsearch mapping issue",
                "Need to fix mapping for 'tickets' index",
                TicketPriority.low,
                TicketState.open);

        // ---------- COMMENTS ----------
        addComments(t1, test1, List.of(
                "I will check backend logs today.",
                "Seems related to recent API refactoring."
        ));
        addComments(t2, test1, List.of(
                "UI error reproduced in Chrome only."
        ));
        addComments(t3, test2, List.of(
                "Rebuilt container with node 20 — still failing.",
                "Maybe missing dependency in Dockerfile."
        ));
        addComments(t4, test2, List.of(
                "Index recreated successfully, need to test sync."
        ));
    }

    // ---------- HELPERS ----------

    private User createIfMissing(UUID id,
                                 String username,
                                 String name,
                                 String surname,
                                 String email,
                                 String rawPassword,
                                 SystemRole role) {
        return users.findById(id).orElseGet(() -> {
            User u = User.builder()
                    .id(id)
                    .username(username)
                    .name(name)
                    .surname(surname)
                    .email(email)
                    .password(encoder.encode(rawPassword))
                    .role(role)
                    .build();
            return users.save(u);
        });
    }

    private Ticket createTicket(Project project,
                                User owner,
                                User assignee,
                                String name,
                                String description,
                                TicketPriority priority,
                                TicketState state) {
        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID())
                .projectId(project.getId())
                .userId(owner.getId())
                .assignedUserId(assignee.getId())
                .name(name)
                .description(description)
                .priority(priority)
                .state(state)
                .type(TicketType.bug)
                .createdAt(OffsetDateTime.now())
                .build();
        return tickets.save(ticket);
    }

    private void addComments(Ticket ticket, User author, List<String> texts) {
        for (String body : texts) {
            TicketComment c = TicketComment.builder()
                    .id(UUID.randomUUID())
                    .ticketId(ticket.getId())
                    .authorId(author.getId())
                    .body(body)
                    .createdAt(OffsetDateTime.now())
                    .build();
            comments.save(c);
        }
    }
}
