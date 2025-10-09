package com.tsystem.web;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ApiError {
    @Builder.Default private OffsetDateTime timestamp = OffsetDateTime.now();
    private int status;           // 404
    private String error;         // Not Found
    private String message;       // human-readable
    private String path;          // /api/...
    private List<FieldViolation> errors; // для валидации

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class FieldViolation {
        private String field;       // name
        private String message;     // must not be blank
    }
}
