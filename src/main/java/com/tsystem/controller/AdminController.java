package com.tsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tsystem.model.dto.request.GenerateDataRequest;
import com.tsystem.model.dto.response.ExportDataDto;
import com.tsystem.model.user.User;
import com.tsystem.service.ExportService;
import com.tsystem.service.GeneratorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class AdminController {

    private final GeneratorService generatorService;
    private final ExportService exportService;


    @PreAuthorize("hasAuthority('admin:create')")
    @PostMapping("/generate-data")
    public ResponseEntity<?> generateData(@AuthenticationPrincipal User user,
                                          @RequestBody GenerateDataRequest request) {

        generatorService.generateData(
                request.getUserCount(),
                request.getProjectCount(),
                request.getTicketsPerUser());

        return ResponseEntity.ok().build();
    }


    // ---------- JSON EXPORT ----------
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/export-json")
    public ResponseEntity<?> exportJson() throws IOException {
        ExportDataDto data = exportService.getExportData();

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String prettyJson = mapper.writeValueAsString(data);
        byte[] jsonBytes = prettyJson.getBytes(StandardCharsets.UTF_8);

        String filename = "export-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) +
                ".json";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonBytes);
    }


}
