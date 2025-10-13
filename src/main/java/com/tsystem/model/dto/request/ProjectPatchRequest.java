package com.tsystem.model.dto.request;

import com.tsystem.model.enums.ProjectStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectPatchRequest {
    @Size(min=1, max=120)
    private String name;

    @Size(max=5000)
    private String description;

    private ProjectStatus status;
}
