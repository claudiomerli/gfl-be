package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.IdDto;
import it.xtreamdev.gflbe.dto.SaveProjectDTO;
import it.xtreamdev.gflbe.dto.SearchProjectDTO;
import it.xtreamdev.gflbe.dto.UpdateProjectDTO;
import it.xtreamdev.gflbe.exception.GLFException;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.ProjectStatus;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.Transient;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final CustomerRepository customerRepository;
    private final NewspaperRepository newspaperRepository;
    private final ProjectContentPreviewRepository projectContentPreviewRepository;
    private final UserRepository userRepository;

    public Page<Project> search(SearchProjectDTO searchProjectDTO, PageRequest pageRequest) {
        return this.projectRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(searchProjectDTO.getGlobalSearch())
                    .ifPresent(globalSearchValue ->
                            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + globalSearchValue.toUpperCase() + "%"))
                    );
            Optional.ofNullable(searchProjectDTO.getCustomerId())
                    .ifPresent(customerIdValue -> {
                        Join<Project, Customer> customerJoin = root.join("customer");
                        predicates.add(criteriaBuilder.equal(customerJoin.get("id"), customerIdValue));
                    });

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, pageRequest);
    }

    @Transactional
    public Project save(SaveProjectDTO saveProjectDTO) {
        Project project = projectRepository.save(
                Project.builder()
                        .name(saveProjectDTO.getName())
                        .customer(
                                customerRepository.findById(saveProjectDTO.getCustomerId())
                                        .orElseThrow(() -> new GLFException("customer not found", HttpStatus.UNPROCESSABLE_ENTITY))
                        )
                        .status(ProjectStatus.CREATED)
                        .build()
        );


        List<ProjectContentPreview> projectContentPreviewList = saveProjectDTO
                .getProjectContentPreviews()
                .stream()
                .map(saveProjectContentPreviewDTO -> {
                    Newspaper newspaper = this.newspaperRepository.findById(saveProjectContentPreviewDTO.getNewspaperId()).orElseThrow(() -> new GLFException("newspaper not found", HttpStatus.UNPROCESSABLE_ENTITY));
                    return ProjectContentPreview
                            .builder()
                            .newspaper(newspaper)
                            .customerNotes(saveProjectContentPreviewDTO.getCustomerNotes())
                            .linkText(saveProjectContentPreviewDTO.getLinkText())
                            .linkUrl(saveProjectContentPreviewDTO.getLinkUrl())
                            .monthUse(saveProjectContentPreviewDTO.getMonthUse())
                            .title(saveProjectContentPreviewDTO.getTitle())
                            .project(project)
                            .build();
                }).collect(Collectors.toList());

        this.projectContentPreviewRepository.saveAll(projectContentPreviewList);
        return project;
    }

    @Transactional
    public Project update(Integer id, UpdateProjectDTO updateProjectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new GLFException("project not found", HttpStatus.NOT_FOUND));
        project.setName(updateProjectDTO.getName());
        project.setCustomer(
                customerRepository.findById(updateProjectDTO.getCustomerId())
                        .orElseThrow(() -> new GLFException("customer not found", HttpStatus.UNPROCESSABLE_ENTITY))
        );

        updateProjectDTO
                .getProjectContentPreviews()
                .forEach(saveProjectContentPreviewDTO -> {
                    Newspaper newspaper = this.newspaperRepository.findById(saveProjectContentPreviewDTO.getNewspaperId())
                            .orElseThrow(() -> new GLFException("newspaper not found", HttpStatus.UNPROCESSABLE_ENTITY));

                    ProjectContentPreview projectContentPreview = new ProjectContentPreview();

                    if (Objects.nonNull(saveProjectContentPreviewDTO.getId())) {
                        projectContentPreview = project.getProjectContentPreviews()
                                .stream()
                                .filter(projectContentPreviewModel -> projectContentPreviewModel.getId().equals(saveProjectContentPreviewDTO.getId()))
                                .findFirst()
                                .orElseThrow(() -> new GLFException("id ProjectContentPreview not found", HttpStatus.BAD_REQUEST));
                    }

                    projectContentPreview.setNewspaper(newspaper);
                    projectContentPreview.setProject(project);
                    projectContentPreview.setCustomerNotes(saveProjectContentPreviewDTO.getCustomerNotes());
                    projectContentPreview.setLinkText(saveProjectContentPreviewDTO.getLinkText());
                    projectContentPreview.setLinkUrl(saveProjectContentPreviewDTO.getLinkUrl());
                    projectContentPreview.setTitle(saveProjectContentPreviewDTO.getTitle());
                    projectContentPreview.setMonthUse(saveProjectContentPreviewDTO.getMonthUse());

                    this.projectContentPreviewRepository.save(projectContentPreview);
                });
        return projectRepository.save(project);
    }

    @Transactional
    public void deletePojectContentPreview(Integer id) {
        this.projectContentPreviewRepository.deleteById(id);
    }

    public Project changeStatus(Integer id, ProjectStatus status) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new GLFException("project not found", HttpStatus.NOT_FOUND));
        return changeStatus(project, status);
    }

    public Project changeStatus(Project project, ProjectStatus status) {
        project.setStatus(status);
        return projectRepository.save(project);
    }

    public Project assignChiefEditor(Integer idProject, IdDto idDto) {
        Project project = this.projectRepository.findById(idProject).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "project id not found"));
        User user = this.userRepository.findById(idDto.getId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "user id not found"));

        if (user.getRole() != RoleName.CHIEF_EDITOR) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "user is not a CHIEF_EDITOR");
        }

        project.setChiefEditor(user);
        project.setStatus(ProjectStatus.ASSIGNED);

        return this.projectRepository.save(project);
    }
}
