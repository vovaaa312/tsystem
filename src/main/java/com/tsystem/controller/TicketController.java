package com.tsystem.controller;


import com.tsystem.model.Ticket;
import com.tsystem.model.dto.TicketCreateRequest;
import com.tsystem.model.dto.TicketUpdateRequest;
import com.tsystem.service.TicketService;
import jakarta.validation.Valid;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public List<Ticket> list(@PathVariable UUID projectId,
                             @AuthenticationPrincipal UserDetails principal) throws ExecutionControl.NotImplementedException {
        return ticketService.list(projectId, principal.getUsername());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket create(@PathVariable UUID projectId,
                         @Valid @RequestBody TicketCreateRequest req,
                         @AuthenticationPrincipal UserDetails principal) {
        return ticketService.create(projectId, req, principal.getUsername());
    }

    @GetMapping("/{ticketId}")
    public Ticket get(@PathVariable UUID projectId, @PathVariable UUID ticketId,
                      @AuthenticationPrincipal UserDetails principal) {
        return ticketService.get(projectId, ticketId, principal.getUsername());
    }

    @PutMapping("/{ticketId}")
    public Ticket update(@PathVariable UUID projectId, @PathVariable UUID ticketId,
                         @Valid @RequestBody TicketUpdateRequest req,
                         @AuthenticationPrincipal UserDetails principal) {
        return ticketService.update(projectId, ticketId, req, principal.getUsername());
    }

    @DeleteMapping("/{ticketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID projectId, @PathVariable UUID ticketId,
                       @AuthenticationPrincipal UserDetails principal) {
        ticketService.delete(projectId, ticketId, principal.getUsername());
    }
}