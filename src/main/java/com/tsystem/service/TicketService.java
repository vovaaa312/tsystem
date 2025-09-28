package com.tsystem.service;

import com.tsystem.exception.NotFoundException;
import com.tsystem.model.Project;
import com.tsystem.model.Ticket;
import com.tsystem.model.user.User;

import com.tsystem.model.dto.TicketCreateRequest;
import com.tsystem.model.dto.TicketUpdateRequest;
import com.tsystem.repository.ProjectRepository;
import com.tsystem.repository.TicketRepository;
import com.tsystem.repository.UserRepository;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private User me(String username) { return userRepository.findByUsername(username).orElseThrow(NotFoundException::new); }

    private Project ownedProject(UUID projectId, String username) {
        User u = me(username);
        return projectRepository.findByIdAndUserId(projectId, u.getId()).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Ticket create(UUID projectId, TicketCreateRequest req, String username) {
        Project p = ownedProject(projectId, username);
        User author = me(username);
        Ticket t = Ticket.builder()
                .name(req.getName())
                .description(req.getDescription())
                .type(req.getType())
                .priority(req.getPriority())
                .project(p)
                .user(author)
                .build();
        return ticketRepository.save(t);
    }

    @Transactional(readOnly = true)
    public List<Ticket> list(UUID projectId, String username) throws ExecutionControl.NotImplementedException {
        Project p = ownedProject(projectId, username);
        throw new ExecutionControl.NotImplementedException("Not Implemented Yet");
//        return ticketRepository.findByProjectIdOrderByCreatedAtDesc(p.getId());
    }

    @Transactional(readOnly = true)
    public Ticket get(UUID projectId, UUID ticketId, String username) {
        Project p = ownedProject(projectId, username);
        return ticketRepository.findByIdAndProjectId(ticketId, p.getId()).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Ticket update(UUID projectId, UUID ticketId, TicketUpdateRequest req, String username) {
        Ticket t = get(projectId, ticketId, username);
        t.setName(req.getName());
        t.setDescription(req.getDescription());
        t.setType(req.getType());
        t.setPriority(req.getPriority());
        t.setState(req.getState());
        return ticketRepository.save(t);
    }

    @Transactional
    public void delete(UUID projectId, UUID ticketId, String username) {
        Ticket t = get(projectId, ticketId, username);
        ticketRepository.delete(t);
    }
}