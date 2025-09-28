package com.tsystem.controller;
import com.tsystem.exception.NotFoundException;
import com.tsystem.model.Project;

import com.tsystem.model.dto.ProjectCreateRequest;
import com.tsystem.model.dto.ProjectUpdateRequest;
import com.tsystem.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<Project> list(@AuthenticationPrincipal UserDetails principal) throws ChangeSetPersister.NotFoundException {
        return projectService.listMine(principal.getUsername());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project create(@Valid @RequestBody ProjectCreateRequest req,
                          @AuthenticationPrincipal UserDetails principal) throws NotFoundException, ChangeSetPersister.NotFoundException {
        return projectService.create(req, principal.getUsername());
    }

    @GetMapping("/{projectId}")
    public Project get(@PathVariable UUID projectId,
                       @AuthenticationPrincipal UserDetails principal) throws ChangeSetPersister.NotFoundException {
        return projectService.getMine(projectId, principal.getUsername());
    }

    @PutMapping("/{projectId}")
    public Project update(@PathVariable UUID projectId,
                          @Valid @RequestBody ProjectUpdateRequest req,
                          @AuthenticationPrincipal UserDetails principal) throws ChangeSetPersister.NotFoundException {
        return projectService.update(projectId, req, principal.getUsername());
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID projectId,
                       @AuthenticationPrincipal UserDetails principal) throws ChangeSetPersister.NotFoundException {
        projectService.delete(projectId, principal.getUsername());
    }
}