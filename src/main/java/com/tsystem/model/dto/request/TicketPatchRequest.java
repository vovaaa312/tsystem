package com.tsystem.model.dto.request;

import com.tsystem.model.enums.TicketPriority;
import com.tsystem.model.enums.TicketState;
import com.tsystem.model.enums.TicketType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketPatchRequest {
    @Size(min = 1, max = 160)
    private String name;

    private TicketType type;
    private TicketPriority priority;
    private TicketState state;

    @Size(max = 10000)
    private String description;
}
