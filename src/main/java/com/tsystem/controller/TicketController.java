package com.tsystem.controller;

import com.tsystem.model.dto.request.TicketCommentRequest;
import com.tsystem.model.dto.request.TicketRequest;
import com.tsystem.model.dto.response.TicketCommentResponse;
import com.tsystem.model.dto.response.TicketHistoryResponse;
import com.tsystem.model.dto.response.TicketResponse;
import com.tsystem.model.mapper.TicketCommentMapper;
import com.tsystem.model.mapper.TicketHistoryMapper;
import com.tsystem.model.mapper.TicketMapper;
import com.tsystem.service.TicketService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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

    // --------- TICKETS ---------

    // GET /projects/{projectId}/tickets
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public List<TicketResponse> list(@PathVariable UUID projectId,
                                     @AuthenticationPrincipal UserDetails principal) {
        return ticketService.list(projectId, principal.getUsername())
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
    }

    // POST /projects/{projectId}/tickets
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    public TicketResponse create(@PathVariable UUID projectId,
                                 @Valid @RequestBody TicketRequest req,
                                 @AuthenticationPrincipal UserDetails principal) {
        return TicketMapper.toResponse(
                ticketService.create(projectId, req, principal.getUsername())
        );
    }

    // GET /projects/{projectId}/tickets/{ticketId}
    @GetMapping("/{ticketId}")
    @SecurityRequirement(name = "bearerAuth")
    public TicketResponse get(@PathVariable UUID projectId,
                              @PathVariable UUID ticketId,
                              @AuthenticationPrincipal UserDetails principal) {
        return TicketMapper.toResponse(
                ticketService.findByIdAndProjectId(projectId, ticketId, principal.getUsername())
        );
    }

    // PUT /projects/{projectId}/tickets/{ticketId}
    @PutMapping("/{ticketId}")
    @SecurityRequirement(name = "bearerAuth")
    public TicketResponse update(@PathVariable UUID projectId,
                                 @PathVariable UUID ticketId,
                                 @Valid @RequestBody TicketRequest req,
                                 @AuthenticationPrincipal UserDetails principal) {
        return TicketMapper.toResponse(
                ticketService.update(projectId, ticketId, req, principal.getUsername())
        );
    }

    // PATCH /projects/{projectId}/tickets/{ticketId}
    @PatchMapping("/{ticketId}")
    @SecurityRequirement(name = "bearerAuth")
    public TicketResponse patch(@PathVariable UUID projectId,
                                @PathVariable UUID ticketId,
                                @Valid @RequestBody TicketRequest req,
                                @AuthenticationPrincipal UserDetails principal) {
        return TicketMapper.toResponse(
                ticketService.patch(projectId, ticketId, req, principal.getUsername())
        );
    }

    // DELETE /projects/{projectId}/tickets/{ticketId}
    @DeleteMapping("/{ticketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    public void delete(@PathVariable UUID projectId,
                       @PathVariable UUID ticketId,
                       @AuthenticationPrincipal UserDetails principal) {
        ticketService.delete(projectId, ticketId, principal.getUsername());
    }

    // PUT /projects/{projectId}/tickets/{ticketId}/assign/{assignedUserId}
    @PutMapping("/{ticketId}/assign/{assignedUserId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerAuth")
    public TicketResponse assign(@PathVariable UUID projectId,
                                 @PathVariable UUID ticketId,
                                 @PathVariable UUID assignedUserId,
                                 @AuthenticationPrincipal UserDetails principal) {
        var t = ticketService.assign(projectId, ticketId, assignedUserId, principal.getUsername());
        return TicketMapper.toResponse(t);
    }

    // --------- COMMENTS ---------

    // GET /projects/{projectId}/tickets/{ticketId}/comments
    @GetMapping("/{ticketId}/comments")
    @SecurityRequirement(name = "bearerAuth")
    public List<TicketCommentResponse> listComments(@PathVariable UUID projectId,
                                                    @PathVariable UUID ticketId,
                                                    @AuthenticationPrincipal UserDetails principal) {
        return TicketCommentMapper.toResponseList(
                ticketService.getComments(projectId, ticketId, principal.getUsername())
        );
    }

    // POST /projects/{projectId}/tickets/{ticketId}/comments
    @PostMapping("/{ticketId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    public TicketCommentResponse addComment(@PathVariable UUID projectId,
                                            @PathVariable UUID ticketId,
                                            @Valid @RequestBody TicketCommentRequest req,
                                            @AuthenticationPrincipal UserDetails principal) {
        return TicketCommentMapper.toResponse(
                ticketService.addComment(projectId, ticketId, req, principal.getUsername())
        );
    }

    // PUT /projects/{projectId}/tickets/{ticketId}/comments/{commentId}
    @PutMapping("/{ticketId}/comments/{commentId}")
    @SecurityRequirement(name = "bearerAuth")
    public TicketCommentResponse updateComment(@PathVariable UUID projectId,
                                               @PathVariable UUID ticketId,
                                               @PathVariable UUID commentId,
                                               @Valid @RequestBody TicketCommentRequest req,
                                               @AuthenticationPrincipal UserDetails principal) {
        return TicketCommentMapper.toResponse(
                ticketService.updateComment(projectId, ticketId, commentId, req, principal.getUsername())
        );
    }

    // DELETE /projects/{projectId}/tickets/{ticketId}/comments/{commentId}
    @DeleteMapping("/{ticketId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    public void deleteComment(@PathVariable UUID projectId,
                              @PathVariable UUID ticketId,
                              @PathVariable UUID commentId,
                              @AuthenticationPrincipal UserDetails principal) {
        ticketService.deleteComment(projectId, ticketId, commentId, principal.getUsername());
    }

    // --------- HISTORY ---------

    // GET /projects/{projectId}/tickets/{ticketId}/history
    @GetMapping("/{ticketId}/history")
    @SecurityRequirement(name = "bearerAuth")
    public List<TicketHistoryResponse> history(@PathVariable UUID projectId,
                                               @PathVariable UUID ticketId,
                                               @AuthenticationPrincipal UserDetails principal) {
        return TicketHistoryMapper.toResponseList(
                ticketService.history(projectId, ticketId, principal.getUsername())
        );
    }
}
