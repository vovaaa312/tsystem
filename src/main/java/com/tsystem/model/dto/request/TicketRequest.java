package com.tsystem.model.dto.request;


import com.tsystem.model.enums.TicketPriority;
import com.tsystem.model.enums.TicketState;
import com.tsystem.model.enums.TicketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketRequest {

    private String name;
    private String description;
    private TicketType type;
    private TicketPriority priority;
    private TicketState state;
    private OffsetDateTime createdAt;
    private UUID projectId;
    private UUID userId;
        private UUID assignedUserId;
}
