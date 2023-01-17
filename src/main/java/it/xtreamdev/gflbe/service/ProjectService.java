package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.project.ProjectListElementDTO;
import it.xtreamdev.gflbe.dto.project.SaveProjectCommissionDTO;
import it.xtreamdev.gflbe.dto.project.SaveProjectDTO;
import it.xtreamdev.gflbe.dto.project.UpdateBulkProjectCommissionStatus;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import it.xtreamdev.gflbe.model.enumerations.ProjectCommissionStatus;
import it.xtreamdev.gflbe.model.enumerations.ProjectStatus;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.ContentRepository;
import it.xtreamdev.gflbe.repository.ProjectCommissionRepository;
import it.xtreamdev.gflbe.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.xlsx4j.exceptions.Xlsx4jException;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.xtreamdev.gflbe.util.ExcelUtils.*;


@Service
@Slf4j
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectCommissionRepository projectCommissionRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NewspaperService newspaperService;

    public Project findById(Integer id) {
        User user = userService.userInfo();
        Project project = this.projectRepository
                .findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
        if (user.getRole().equals(RoleName.CUSTOMER) && !user.getId().equals(project.getCustomer().getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "User not allowed to view this project");
        }

        return project;
    }

    public Project save(SaveProjectDTO saveProjectDTO) {
        Project project = Project.builder()
                .name(saveProjectDTO.getName())
                .build();
        project.getProjectStatusChanges().add(
                ProjectStatusChange
                        .builder()
                        .project(project)
                        .projectStatus(ProjectStatus.CREATED)
                        .build()
        );

        return this.projectRepository.save(project);
    }

    public Project addCommission(Integer projectId, SaveProjectCommissionDTO saveProjectCommissionDTO) {
        Project project = this.findById(projectId);
        ProjectCommission projectCommission = ProjectCommission.builder()
                .newspaper(this.newspaperService.findById(saveProjectCommissionDTO.getNewspaperId()))
                .period(saveProjectCommissionDTO.getPeriod())
                .year(saveProjectCommissionDTO.getYear())
                .anchor(saveProjectCommissionDTO.getAnchor())
                .isAnchorBold(saveProjectCommissionDTO.getIsAnchorBold())
                .isAnchorItalic(saveProjectCommissionDTO.getIsAnchorItalic())
                .status(ProjectCommissionStatus.CREATED)
                .url(saveProjectCommissionDTO.getUrl())
                .title(saveProjectCommissionDTO.getTitle())
                .notes(saveProjectCommissionDTO.getNotes())
                .publicationUrl(saveProjectCommissionDTO.getPublicationUrl())
                .publicationDate(saveProjectCommissionDTO.getPublicationDate())
                .project(project)
                .build();
        projectCommission.getProjectStatusChanges().add(
                ProjectStatusChange
                        .builder()
                        .projectCommissionStatus(ProjectCommissionStatus.CREATED)
                        .projectCommission(projectCommission)
                        .build()
        );
        projectCommission.setContent(Content.builder()
                .contentStatus(ContentStatus.WORKING)
                .projectCommission(projectCommission)
                .build());

        project.getProjectCommissions()
                .add(projectCommission);

        if (project.getStatus() == ProjectStatus.SENT_TO_ADMINISTRATION || project.getStatus() == ProjectStatus.INVOICED) {
            project.setStatus(ProjectStatus.CREATED);
            project.getProjectStatusChanges().add(ProjectStatusChange
                    .builder()
                    .projectStatus(ProjectStatus.CREATED)
                    .project(project)
                    .build());
        }

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
                    projectCommission.setYear(saveProjectCommissionDTO.getYear());
                    projectCommission.setAnchor(saveProjectCommissionDTO.getAnchor());
                    projectCommission.setIsAnchorBold(saveProjectCommissionDTO.getIsAnchorBold());
                    projectCommission.setIsAnchorItalic(saveProjectCommissionDTO.getIsAnchorItalic());
                    projectCommission.setUrl(saveProjectCommissionDTO.getUrl());
                    projectCommission.setTitle(saveProjectCommissionDTO.getTitle());
                    projectCommission.setNotes(saveProjectCommissionDTO.getNotes());
                    projectCommission.setPublicationUrl(saveProjectCommissionDTO.getPublicationUrl());
                    projectCommission.setPublicationDate(saveProjectCommissionDTO.getPublicationDate());
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

    @Transactional
    public Project setStatusCommission(Integer projectId, Integer commissionId, String status) {
        Project project = this.findById(projectId);
        project.getProjectCommissions().stream().filter(projectCommission -> projectCommission.getId().equals(commissionId))
                .findFirst()
                .ifPresent(projectCommission -> {
                    projectCommission.setStatus(ProjectCommissionStatus.valueOf(status));
                    projectCommission.getProjectStatusChanges().add(ProjectStatusChange.builder()
                            .projectCommissionStatus(projectCommission.getStatus())
                            .projectCommission(projectCommission)
                            .build());
                });
        this.projectRepository.save(project);

        if (project.getProjectCommissions().stream().allMatch(projectCommission -> projectCommission.getStatus() == ProjectCommissionStatus.SENT_TO_ADMINISTRATION)) {
            project.setStatus(ProjectStatus.SENT_TO_ADMINISTRATION);
            project.getProjectStatusChanges().add(ProjectStatusChange
                    .builder()
                    .projectStatus(ProjectStatus.SENT_TO_ADMINISTRATION)
                    .project(project)
                    .build());
        } else if (project.getStatus() == ProjectStatus.SENT_TO_ADMINISTRATION) {
            project.setStatus(ProjectStatus.CREATED);
            project.getProjectStatusChanges().add(ProjectStatusChange
                    .builder()
                    .projectStatus(ProjectStatus.CREATED)
                    .project(project)
                    .build());
        }

        return this.projectRepository.save(project);
    }

    public Project invoiceProject(Integer projectId) {
        Project project = this.findById(projectId);
        project.setStatus(ProjectStatus.INVOICED);
        project.getProjectStatusChanges().add(ProjectStatusChange
                .builder()
                .projectStatus(ProjectStatus.INVOICED)
                .project(project)
                .build());
        return this.projectRepository.save(project);
    }

    public Page<ProjectListElementDTO> find(String globalSearch, String status, Pageable pageable) {
        User user = this.userService.userInfo();
        return this.projectRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            Join<Project, ProjectCommission> projectCommissionsJoin = root.join("projectCommissions", JoinType.LEFT);
            Join<Project, User> customerJoin = root.join("customer", JoinType.LEFT);
            criteriaQuery.distinct(true);


            if (StringUtils.isNotBlank(status)) {
                predicateList.add(criteriaBuilder.equal(root.get("status"), ProjectStatus.valueOf(status)));
            }

            switch (user.getRole()) {
                case PUBLISHER:
                    CriteriaBuilder.In<Object> inClausePublisher = criteriaBuilder.in(projectCommissionsJoin.get("status"));
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
        }, pageable).map(project -> project.toListElement());
    }

    @Transactional
    public void setBulkStatusCommission(Integer id, String status, UpdateBulkProjectCommissionStatus updateBulkProjectCommissionStatus) {
        updateBulkProjectCommissionStatus.getIds().forEach(idCommission -> this.setStatusCommission(id, idCommission, status));
    }

    public void createMissingContent() {
        this.projectCommissionRepository.findByContentIsNull().forEach(projectCommission -> this.contentRepository.save(
                Content.builder()
                        .contentStatus(ContentStatus.WORKING)
                        .projectCommission(projectCommission)
                        .build()
        ));

    }

    public byte[] exportProjectExcel(Integer projectId) {
        Project project = this.findById(projectId);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            SpreadsheetMLPackage spreadsheet = createSpreadsheet();
            SheetData sheet = addSheet(spreadsheet, project.getName());
            addRow(sheet, "Testata", "Url di pubblicazione", "Data di pubblicazione", "Periodo");
            project.getProjectCommissions().forEach(projectCommission -> {
                addRow(sheet,
                        projectCommission.getNewspaper().getName(),
                        projectCommission.getPublicationUrl(),
                        projectCommission.getPublicationDate(),
                        projectCommission.getPeriod());
            });

            spreadsheet.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error", e);
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Error creating excel");
        }
    }
}
