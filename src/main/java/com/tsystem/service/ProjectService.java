package com.tsystem.service;

import com.tsystem.exception.NotFoundException;
import com.tsystem.model.Project;
import com.tsystem.model.user.User;

import com.tsystem.model.dto.ProjectCreateRequest;
import com.tsystem.model.dto.ProjectUpdateRequest;
import com.tsystem.repository.ProjectRepository;
import com.tsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projects;
    private final UserRepository users;

    private User byUsername(String username) throws ChangeSetPersister.NotFoundException {
        return users.findByUsername(username).orElseThrow(ChangeSetPersister.NotFoundException::new);
    }

    @Transactional
    public Project create(ProjectCreateRequest req, String currentUsername) throws ChangeSetPersister.NotFoundException {
        User owner = byUsername(currentUsername);
        Project p = Project.builder()
                .name(req.getName())
                .description(req.getDescription())
                .user(owner)
                .build();
        return projects.save(p);
    }

    @Transactional(readOnly = true)
    public List<Project> listMine(String currentUsername) throws ChangeSetPersister.NotFoundException {
        User me = byUsername(currentUsername);
        return projects.findByUserIdOrderByCreatedAtDesc(me.getId());
    }

    @Transactional(readOnly = true)
    public Project getMine(UUID id, String currentUsername) throws ChangeSetPersister.NotFoundException {
        User me = byUsername(currentUsername);
        return projects.findByIdAndUserId(id, me.getId()).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Project update(UUID id, ProjectUpdateRequest req, String currentUsername) throws ChangeSetPersister.NotFoundException {
        Project p = getMine(id, currentUsername);
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setStatus(req.getStatus());
        return projects.save(p);
    }

    @Transactional
    public void delete(UUID id, String currentUsername) throws ChangeSetPersister.NotFoundException {
        Project p = getMine(id, currentUsername);
        projects.delete(p);
    }
}