package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.project.SaveProjectCommissionDTO;
import it.xtreamdev.gflbe.dto.project.SaveProjectDTO;
import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.model.ProjectCommission;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.ProjectCommissionStatus;
import it.xtreamdev.gflbe.model.enumerations.ProjectStatus;
import it.xtreamdev.gflbe.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private NewspaperService newspaperService;

    public Project findById(Integer id) {
        return this.projectRepository
                .findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
    }

    public Project save(SaveProjectDTO saveProjectDTO) {
        return this.projectRepository.save(
                Project.builder()
                        .name(saveProjectDTO.getName())
                        .build()
        );
    }

    public Project addCommission(Integer projectId, SaveProjectCommissionDTO saveProjectCommissionDTO) {
        Project project = this.findById(projectId);
        project.getProjectCommissions()
                .add(ProjectCommission.builder()
                        .newspaper(this.newspaperService.findById(saveProjectCommissionDTO.getNewspaperId()))
                        .period(saveProjectCommissionDTO.getPeriod())
                        .anchor(saveProjectCommissionDTO.getAnchor())
                        .status(ProjectCommissionStatus.CREATED)
                        .url(saveProjectCommissionDTO.getUrl())
                        .title(saveProjectCommissionDTO.getTitle())
                        .notes(saveProjectCommissionDTO.getNotes())
                        .publicationUrl(saveProjectCommissionDTO.getPublicationUrl())
                        .project(project)
                        .build());

        return this.projectRepository.save(project);
    }

    public Project removeCommission(Integer projectId, Integer commissionId) {
        Project project = this.findById(projectId);
        project.getProjectCommissions()
                .removeIf(projectCommission -> projectCommission.getId().equals(commissionId));
        return this.projectRepository.save(project);
    }

    public Project updateCommission(Integer projectId, Integer commissionId, SaveProjectCommissionDTO saveProjectCommissionDTO) {
        Project project = this.findById(projectId);
        project.getProjectCommissions().stream().filter(projectCommission -> projectCommission.getId().equals(commissionId))
                .findFirst()
                .ifPresent(projectCommission -> {
                    projectCommission.setNewspaper(this.newspaperService.findById(saveProjectCommissionDTO.getNewspaperId()));
                    projectCommission.setPeriod(saveProjectCommissionDTO.getPeriod());
                    projectCommission.setAnchor(saveProjectCommissionDTO.getAnchor());
                    projectCommission.setUrl(saveProjectCommissionDTO.getUrl());
                    projectCommission.setTitle(saveProjectCommissionDTO.getTitle());
                    projectCommission.setNotes(saveProjectCommissionDTO.getNotes());
                    projectCommission.setPublicationUrl(saveProjectCommissionDTO.getPublicationUrl());
                });

        return this.projectRepository.save(project);
    }

    public Project update(Integer projectId, SaveProjectDTO saveProjectDTO) {
        Project project = this.findById(projectId);
        project.setName(saveProjectDTO.getName());
        project.setCustomer(userService.findById(saveProjectDTO.getCustomerId()));
        project.setBillingAmount(saveProjectDTO.getBillingAmount());
        project.setBillingDescription(saveProjectDTO.getBillingDescription());
        project.setExpiration(saveProjectDTO.getExpiration());
        return this.projectRepository.save(project);
    }

    public void delete(Integer projectId) {
        this.projectRepository.deleteById(projectId);
    }

    public Project setStatusCommission(Integer projectId, Integer commissionId, String status) {
        Project project = this.findById(projectId);
        project.getProjectCommissions().stream().filter(projectCommission -> projectCommission.getId().equals(commissionId))
                .findFirst()
                .ifPresent(projectCommission -> {
                    projectCommission.setStatus(ProjectCommissionStatus.valueOf(status));
                });

        if (project.getProjectCommissions().stream().allMatch(projectCommission -> projectCommission.getStatus() == ProjectCommissionStatus.SENT_TO_ADMINISTRATION)) {
            project.setStatus(ProjectStatus.SENT_TO_ADMINISTRATION);
        }

        return this.projectRepository.save(project);
    }

    public Project closeProject(Integer projectId) {
        Project project = this.findById(projectId);
        project.setStatus(ProjectStatus.CLOSED);
        return this.projectRepository.save(project);
    }

    public Page<Project> find(String globalSearch, String status, Pageable pageable) {
        User user = this.userService.userInfo();
        return this.projectRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            Join<Project, ProjectCommission> projectCommissions = root.join("projectCommissions", JoinType.LEFT);
            Join<Project, User> customerJoin = root.join("customer", JoinType.LEFT);
            criteriaQuery.distinct(true);

            switch (user.getRole()) {
                case ADMIN:
                    if (StringUtils.isNotBlank(status)) {
                        predicateList.add(criteriaBuilder.equal(root.get("status"), ProjectStatus.valueOf(status)));
                    }
                    break;
                case CHIEF_EDITOR:
                    CriteriaBuilder.In<Object> inClauseChiefEditor = criteriaBuilder.in(projectCommissions.get("status"));
                    Arrays.asList(ProjectCommissionStatus.STARTED, ProjectCommissionStatus.ASSIGNED, ProjectCommissionStatus.STANDBY_EDITORIAL, ProjectCommissionStatus.TO_PUBLISH, ProjectCommissionStatus.SENT_TO_NEWSPAPER, ProjectCommissionStatus.STANDBY_PUBLICATION, ProjectCommissionStatus.SENT_TO_ADMINISTRATION).forEach(inClauseChiefEditor::value);
                    predicateList.add(inClauseChiefEditor);
                    break;
                case PUBLISHER:
                    CriteriaBuilder.In<Object> inClausePublisher = criteriaBuilder.in(projectCommissions.get("status"));
                    Arrays.asList(ProjectCommissionStatus.TO_PUBLISH, ProjectCommissionStatus.SENT_TO_NEWSPAPER, ProjectCommissionStatus.STANDBY_PUBLICATION, ProjectCommissionStatus.SENT_TO_ADMINISTRATION).forEach(inClausePublisher::value);
                    predicateList.add(inClausePublisher);
                    break;
                case ADMINISTRATION:
                    predicateList.add(criteriaBuilder.equal(root.get("status"), ProjectStatus.SENT_TO_ADMINISTRATION));
                    break;
                case CUSTOMER:
                    predicateList.add(criteriaBuilder.equal(root.get("customer"), user.getId()));
                    break;
            }

            if (StringUtils.isNotBlank(globalSearch)) {
                Arrays.stream(globalSearch.split(" ")).forEach(s -> {
                    predicateList.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + s.toUpperCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(customerJoin.get("fullname")), "%" + s.toUpperCase() + "%")
                    ));
                });
            }

            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        }, pageable);
    }
}
