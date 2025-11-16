package com.tsystem.service;

import com.tsystem.model.Project;
import com.tsystem.model.Ticket;
import com.tsystem.model.dto.request.ProjectCreateRequest;
import com.tsystem.model.dto.request.RegisterRequest;
import com.tsystem.model.dto.request.TicketCommentRequest;
import com.tsystem.model.dto.request.TicketRequest;
import com.tsystem.model.enums.TicketPriority;
import com.tsystem.model.enums.TicketState;
import com.tsystem.model.enums.TicketType;
import com.tsystem.model.user.SystemRole;
import com.tsystem.model.user.User;
import com.tsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GeneratorService {

    private final AuthService authService;
    private final UserRepository userRepository;

    private final ProjectService projectService;
    private final TicketService ticketService;

    private final Faker faker = new Faker(new Locale("en-EN"));
    private final Random random = new Random();

    // --------------------------------------------------------------------
    // PUBLIC ENTRY
    // --------------------------------------------------------------------
    @Transactional
    public void generateData(int userCount, int projectCount, int ticketsPerUser) {

        List<User> users = generateUsers(userCount);

        User owner = users.get(0);                   // A) first = project owner
        List<User> contributors = users.subList(1, users.size());

//        List<Project> projects = generateProjects(owner, contributors, projectCount);
        List<Project> projects = generateProjects(owner, projectCount);
        for (Project p : projects) {
            generateTicketsForProject(p, owner, users, ticketsPerUser);
        }
    }

    // --------------------------------------------------------------------
    // USERS
    // --------------------------------------------------------------------
    private List<User> generateUsers(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createUser())
                .collect(Collectors.toList());
    }

    private User createUser() {

        String username = null;
        String email = null;

        // --- generate unique username ---
        while (true) {
            String candidate = faker.name().username();
            if (userRepository.findByUsername(candidate).isEmpty()) {
                username = candidate;
                break;
            }
        }

        // --- generate unique email ---
        while (true) {
            String candidate = faker.internet().emailAddress();
            if (userRepository.findByEmail(candidate).isEmpty()) {
                email = candidate;
                break;
            }
        }

        RegisterRequest req = RegisterRequest.builder()
                .username(username)
                .email(email)
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .password("password")
                .build();

        authService.register(req);

        // Fix: must use effectively final variable
        final String finalUsername = username;

        return userRepository.findByUsername(finalUsername)
                .orElseThrow(() -> new IllegalStateException("Failed to create: " + finalUsername));
    }



    // --------------------------------------------------------------------
    // PROJECTS
    // --------------------------------------------------------------------
    private List<Project> generateProjects(User owner,  int projectCount) {
        List<Project> list = new ArrayList<>();

        for (int i = 0; i < projectCount; i++) {

            ProjectCreateRequest req = ProjectCreateRequest.builder()
                    .name("Project " + faker.company().industry())
                    .description(faker.lorem().sentence())
                    .build();

            Project project = projectService.create(req, owner.getUsername());

            list.add(project);
        }

        return list;
    }

    // --------------------------------------------------------------------
    // TICKETS + COMMENTS (via TicketService)
    // --------------------------------------------------------------------
    private void generateTicketsForProject(Project project, User owner, List<User> allUsers, int ticketsPerUser) {

        // исполнители = все, кроме владельца проекта
        List<User> assignees = allUsers.stream()
                .filter(u -> !u.getId().equals(owner.getId()))
                .toList();

        // защита — список исполнителей не пустой
        if (assignees.isEmpty()) {
            throw new IllegalStateException("At least 2 users required: project owner + assignee");
        }

        for (int i = 0; i < ticketsPerUser; i++) {
            for (User ignored : allUsers) {

                // выбираем случайного исполнителя
                User assigned = assignees.get(random.nextInt(assignees.size()));

                Ticket ticket = createTicket(project, owner, assigned);

                addCommentToTicket(project, ticket, owner);
            }
        }
    }



    private Ticket createTicket(Project project, User creator, User assigned) {

        TicketRequest req = TicketRequest.builder()
                .name(faker.company().buzzword() + " " + faker.lorem().word())
                .description(faker.lorem().sentence())
                .priority(randomPriority())
                .state(TicketState.open)
                .type(randomType())
                .assignedUserId(assigned.getId())
                .build();

        return ticketService.create(project.getId(), req, creator.getUsername());
    }

    private TicketType randomType() {
        TicketType[] arr = {TicketType.bug, TicketType.task, TicketType.feature};
        return arr[random.nextInt(arr.length)];
    }

    private TicketPriority randomPriority() {
        TicketPriority[] arr = {TicketPriority.low, TicketPriority.med, TicketPriority.high};
        return arr[random.nextInt(arr.length)];
    }

    private void addCommentToTicket(Project project, Ticket ticket, User author) {
        TicketCommentRequest req = TicketCommentRequest.builder()
                .body(faker.lorem().sentence())
                .build();

        ticketService.addComment(
                project.getId(),
                ticket.getId(),
                req,
                author.getUsername()
        );
    }
}
