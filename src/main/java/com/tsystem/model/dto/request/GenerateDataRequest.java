package com.tsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateDataRequest {

    @NotBlank
    private int userCount;
    @NotBlank
    private int projectCount;
    @NotBlank
    private int ticketsPerUser;
}
