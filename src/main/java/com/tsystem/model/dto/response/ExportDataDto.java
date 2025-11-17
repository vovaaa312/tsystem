package com.tsystem.model.dto.response;

import com.tsystem.model.Project;
import com.tsystem.model.Ticket;
import com.tsystem.model.TicketComment;
import com.tsystem.model.TicketHistory;
import com.tsystem.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ExportDataDto {

    private List<User> users;
    private List<Project> projects;
    private List<Ticket> tickets;
    private List<TicketComment> ticketComments;
    private List<TicketHistory> ticketHistories;
}
