package com.tsystem.service;

import com.tsystem.exception.ForbiddenException;
import com.tsystem.exception.NotFoundException;
import com.tsystem.model.Project;
import com.tsystem.model.Ticket;
import com.tsystem.model.TicketComment;
import com.tsystem.model.TicketHistory;
import com.tsystem.model.dto.request.TicketCommentRequest;
import com.tsystem.model.dto.request.TicketRequest;
import com.tsystem.model.enums.TicketPriority;
import com.tsystem.model.enums.TicketState;
import com.tsystem.model.user.User;
import com.tsystem.repository.ProjectRepository;
import com.tsystem.repository.TicketCommentRepository;
import com.tsystem.repository.TicketHistoryRepository;
import com.tsystem.repository.TicketRepository;
import com.tsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository tickets;
    private final ProjectRepository projects;
    private final UserRepository users;
    private final TicketCommentRepository ticketComments;
    private final TicketHistoryRepository ticketHistories;

    // --------- TICKETS ---------

    @Transactional(readOnly = true)
    public List<Ticket> list(UUID projectId, String username) {
        mustOwnProject(projectId, username);
        return tickets.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    @Transactional(readOnly = true)
    public List<Ticket> listByAssigned(String username) {

        return tickets.findAllByAssignedUserId(me(username).getId()).orElse(null);
    }

    @Transactional(readOnly = true)
    public Ticket findByIdAndProjectId(UUID projectId, UUID ticketId, String username) {
        mustOwnProject(projectId, username);
        return tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Ticket create(UUID projectId, TicketRequest req, String username) {
        Project project = mustOwnProject(projectId, username);
        User creator = me(username);

        TicketState state = req.getState() != null ? req.getState() : TicketState.open;
        TicketPriority priority = req.getPriority() != null ? req.getPriority() : TicketPriority.med;

        Ticket ticket = Ticket.builder()
                .name(req.getName())
                .description(req.getDescription())
                .type(req.getType())
                .priority(priority)
                .state(state)
                .projectId(project.getId())
                .userId(creator.getId())
                .assignedUserId(req.getAssignedUserId())
                .build();

        Ticket saved = tickets.save(ticket);

        registerHistory(saved,
                null, saved.getState(),
                null, saved.getPriority(),
                null, saved.getAssignedUserId(),
                creator);

        return saved;
    }

    @Transactional
    public Ticket update(UUID projectId, UUID ticketId, TicketRequest req, String username) {
        mustOwnProject(projectId, username);
        Ticket t = tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(NotFoundException::new);

        User actor = me(username);

        TicketState oldState = t.getState();
        TicketPriority oldPriority = t.getPriority();
        UUID oldAssignee = t.getAssignedUserId();

        t.setName(req.getName());
        t.setDescription(req.getDescription());
        t.setType(req.getType());
        t.setPriority(req.getPriority());
        t.setState(req.getState());
        t.setAssignedUserId(req.getAssignedUserId());

        Ticket saved = tickets.save(t);
        registerHistoryIfChanged(saved, oldState, oldPriority, oldAssignee, actor);

        return saved;
    }

    @Transactional
    public Ticket patch(UUID projectId, UUID ticketId, TicketRequest req, String username) {
        mustOwnProject(projectId, username);
        Ticket t = tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(NotFoundException::new);

        User actor = me(username);

        TicketState oldState = t.getState();
        TicketPriority oldPriority = t.getPriority();
        UUID oldAssignee = t.getAssignedUserId();

        if (req.getName() != null) {
            t.setName(req.getName());
        }
        if (req.getDescription() != null) {
            t.setDescription(req.getDescription());
        }
        if (req.getType() != null) {
            t.setType(req.getType());
        }
        if (req.getPriority() != null) {
            t.setPriority(req.getPriority());
        }
        if (req.getState() != null) {
            t.setState(req.getState());
        }
        if (req.getAssignedUserId() != null) {
            t.setAssignedUserId(req.getAssignedUserId());
        }

        Ticket saved = tickets.save(t);
        registerHistoryIfChanged(saved, oldState, oldPriority, oldAssignee, actor);

        return saved;
    }

    @Transactional
    public void delete(UUID projectId, UUID ticketId, String username) {
        mustOwnProject(projectId, username);
        Ticket t = tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(NotFoundException::new);
        tickets.delete(t);
    }

    @Transactional
    public Ticket assign(UUID projectId, UUID ticketId, UUID assignedUserId, String username) {
        mustOwnProject(projectId, username);
        Ticket t = tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(NotFoundException::new);

        User actor = me(username);

        TicketState oldState = t.getState();
        TicketPriority oldPriority = t.getPriority();
        UUID oldAssignee = t.getAssignedUserId();

        t.setAssignedUserId(assignedUserId);

        Ticket saved = tickets.save(t);
        registerHistoryIfChanged(saved, oldState, oldPriority, oldAssignee, actor);

        return saved;
    }

    // --------- HISTORY ---------

    @Transactional(readOnly = true)
    public List<TicketHistory> history(UUID projectId, UUID ticketId, String username) {
        mustOwnProject(projectId, username);
        Ticket t = tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(NotFoundException::new);
        return ticketHistories.findByTicket_IdOrderByChangedAtAsc(t.getId());
    }

    private void registerHistoryIfChanged(
            Ticket ticket,
            TicketState oldState,
            TicketPriority oldPriority,
            UUID oldAssigneeId,
            User changedBy
    ) {
        TicketState newState = ticket.getState();
        TicketPriority newPriority = ticket.getPriority();
        UUID newAssigneeId = ticket.getAssignedUserId();

        boolean stateChanged = (oldState != null || newState != null)
                && (oldState == null || !oldState.equals(newState));
        boolean priorityChanged = (oldPriority != null || newPriority != null)
                && (oldPriority == null || !oldPriority.equals(newPriority));
        boolean assigneeChanged = (oldAssigneeId != null || newAssigneeId != null)
                && (oldAssigneeId == null || !oldAssigneeId.equals(newAssigneeId));

        if (stateChanged || priorityChanged || assigneeChanged) {
            registerHistory(ticket,
                    oldState, newState,
                    oldPriority, newPriority,
                    oldAssigneeId, newAssigneeId,
                    changedBy);
        }
    }

    private void registerHistory(
            Ticket ticket,
            TicketState oldState,
            TicketState newState,
            TicketPriority oldPriority,
            TicketPriority newPriority,
            UUID oldAssigneeId,
            UUID newAssigneeId,
            User changedBy
    ) {
        User oldAssignee = oldAssigneeId != null
                ? users.findById(oldAssigneeId).orElse(null)
                : null;
        User newAssignee = newAssigneeId != null
                ? users.findById(newAssigneeId).orElse(null)
                : null;

        TicketHistory history = TicketHistory.builder()
                .ticket(ticket)
                .changedBy(changedBy)
                .oldState(oldState)
                .newState(newState)
                .oldPriority(oldPriority)
                .newPriority(newPriority)
                .oldAssignee(oldAssignee)
                .newAssignee(newAssignee)
                .build();

        ticketHistories.save(history);
    }

    // --------- COMMENTS ---------

    @Transactional(readOnly = true)
    public List<TicketComment> getComments(UUID projectId, UUID ticketId, String username) {
        mustOwnProject(projectId, username);
        tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(NotFoundException::new);
        return ticketComments.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }

    @Transactional
    public TicketComment addComment(UUID projectId, UUID ticketId, TicketCommentRequest request, String username) {
        mustOwnProject(projectId, username);
        tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(NotFoundException::new);
        User author = me(username);

        TicketComment comment = TicketComment.builder()
                .ticketId(ticketId)
                .authorId(author.getId())
                .body(request.getBody())
                .build();

        return ticketComments.save(comment);
    }

    @Transactional
    public TicketComment updateComment(UUID projectId,
                                       UUID ticketId,
                                       UUID commentId,
                                       TicketCommentRequest request,
                                       String username) {
        mustOwnProject(projectId, username);
        tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(NotFoundException::new);

        TicketComment comment = ticketComments.findById(commentId)
                .orElseThrow(NotFoundException::new);

        User actor = me(username);
        if (!comment.getAuthorId().equals(actor.getId())) {
            throw new ForbiddenException();
        }

        comment.setBody(request.getBody());
        return ticketComments.save(comment);
    }

    @Transactional
    public void deleteComment(UUID projectId,
                              UUID ticketId,
                              UUID commentId,
                              String username) {
        mustOwnProject(projectId, username);
        tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(NotFoundException::new);

        TicketComment comment = ticketComments.findById(commentId)
                .orElseThrow(NotFoundException::new);

        User actor = me(username);
        if (!comment.getAuthorId().equals(actor.getId())) {
            throw new ForbiddenException();
        }

        ticketComments.delete(comment);
    }

    // --------- HELPERS ---------

    private User me(String username) {
        return users.findByUsername(username).orElseThrow(NotFoundException::new);
    }

    private Project mustOwnProject(UUID projectId, String username) {
        Project p = projects.findById(projectId).orElseThrow(NotFoundException::new);
        if (!p.getUser().getUsername().equals(username)) {
            throw new ForbiddenException();
        }
        return p;
    }
}
