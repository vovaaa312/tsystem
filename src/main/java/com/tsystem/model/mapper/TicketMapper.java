package com.tsystem.model.mapper;


import com.tsystem.model.Ticket;
import com.tsystem.model.dto.request.TicketRequest;
import com.tsystem.model.dto.response.TicketResponse;

public final class TicketMapper {
    private TicketMapper(){}

    public static TicketResponse toResponse(Ticket t) {
        return TicketResponse.builder()
                .id(t.getId())
                .name(t.getName())
                .description(t.getDescription())
                .type(t.getType())
                .priority(t.getPriority())
                .state(t.getState())
                .createdAt(t.getCreatedAt())
                .projectId(t.getProjectId())
                .userId(t.getUserId())
                .assignedUserId(t.getAssignedUserId())
                .build();
    }

    public static Ticket fromRequest(TicketRequest r) {
        return Ticket.builder()
                .name(r.getName())
                .description(r.getDescription())
                .type(r.getType())
                .priority(r.getPriority())
                .state(r.getState())
                .projectId(r.getProjectId())
                .userId(r.getUserId())
                .assignedUserId(r.getAssignedUserId()) // new
                .build();
    }
}
