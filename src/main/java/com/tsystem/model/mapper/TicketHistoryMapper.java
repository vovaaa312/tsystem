package com.tsystem.model.mapper;

import com.tsystem.model.TicketHistory;
import com.tsystem.model.dto.response.TicketHistoryResponse;

import java.util.List;
import java.util.stream.Collectors;

public final class TicketHistoryMapper {

    private TicketHistoryMapper() {
    }

    public static TicketHistoryResponse toResponse(TicketHistory h) {
        return TicketHistoryResponse.builder()
                .id(h.getId())
                .ticketId(h.getTicket().getId())
                .changedById(h.getChangedBy().getId())
                .oldState(h.getOldState())
                .newState(h.getNewState())
                .oldPriority(h.getOldPriority())
                .newPriority(h.getNewPriority())
                .oldAssigneeId(h.getOldAssignee() != null ? h.getOldAssignee().getId() : null)
                .newAssigneeId(h.getNewAssignee() != null ? h.getNewAssignee().getId() : null)
                .changedAt(h.getChangedAt())
                .build();
    }

    public static List<TicketHistoryResponse> toResponseList(List<TicketHistory> history) {
        return history.stream()
                .map(TicketHistoryMapper::toResponse)
                .collect(Collectors.toList());
    }
}
