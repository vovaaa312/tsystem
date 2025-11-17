package com.tsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tsystem.model.Project;
import com.tsystem.model.Ticket;
import com.tsystem.model.TicketComment;
import com.tsystem.model.TicketHistory;
import com.tsystem.model.dto.response.ExportDataDto;
import com.tsystem.model.user.User;
import com.tsystem.repository.ProjectRepository;
import com.tsystem.repository.TicketCommentRepository;
import com.tsystem.repository.TicketHistoryRepository;
import com.tsystem.repository.TicketRepository;
import com.tsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TicketRepository ticketRepository;
    private final TicketCommentRepository ticketCommentRepository;
    private final TicketHistoryRepository ticketHistoryRepository;

    @Transactional(readOnly = true)
    public ExportDataDto getExportData() {
        return ExportDataDto.builder()
                .users(userRepository.findAll())
                .projects(projectRepository.findAll())
                .tickets(ticketRepository.findAll())
                .ticketComments(ticketCommentRepository.findAll())
                .ticketHistories(ticketHistoryRepository.findAll())
                .build();
    }
}
