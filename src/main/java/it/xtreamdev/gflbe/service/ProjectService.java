package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.content.SaveAttachmentDTO;
import it.xtreamdev.gflbe.dto.content.SaveProjectCommissionHintDTO;
import it.xtreamdev.gflbe.dto.project.ProjectListElementDTO;
import it.xtreamdev.gflbe.dto.project.SaveProjectCommissionDTO;
import it.xtreamdev.gflbe.dto.project.SaveProjectDTO;
import it.xtreamdev.gflbe.dto.project.UpdateBulkProjectCommissionStatus;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import it.xtreamdev.gflbe.model.enumerations.ProjectCommissionStatus;
import it.xtreamdev.gflbe.model.enumerations.ProjectStatus;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.xlsx4j.sml.SheetData;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.function.Consumer;

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
    private ContentHintRepository contentHintRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NewspaperService newspaperService;

    public Project findById(Integer id) {
        User user = userService.userInfo();
        Project project = this.projectRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
        if (user.getRole().equals(RoleName.CUSTOMER) && !user.getId().equals(project.getCustomer().getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "User not allowed to view this project");
        }

        return project;
    }

    public void createProjectFromDomain(Domain domain){
        Project project = Project.builder()
                .name(domain.getName())
                .domain(domain)
                .hint(ContentHint.builder().build())
                .build();
        project.getProjectStatusChanges().add(
                ProjectStatusChange
                        .builder()
                        .project(project)
                        .projectStatus(ProjectStatus.CREATED)
                        .build()
        );

        this.projectRepository.save(project);
    }

    @Transactional
    public void removeReferenceFromDomain(Domain domain){
        this.projectRepository.setDomainToNullWhereDomain(domain);
    }

    public Project save(SaveProjectDTO saveProjectDTO) {
        Project project = Project.builder()
                .name(saveProjectDTO.getName())
                .hint(ContentHint.builder().build())
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
                .period(Month.valueOf(saveProjectCommissionDTO.getPeriod()))
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

        Content contentForCommission = Content.builder()
                .contentStatus(ContentStatus.WORKING)
                .projectCommission(projectCommission)
                .hint(ContentHint.builder().build())
                .build();

        projectCommission.setContent(contentForCommission);
        project.getProjectCommissions().add(projectCommission);

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
                    projectCommission.setPeriod(Month.valueOf(saveProjectCommissionDTO.getPeriod()));
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
        project.setCustomer(saveProjectDTO.getCustomerId() != null ? userService.findById(saveProjectDTO.getCustomerId()): null);
        project.setBillingAmount(saveProjectDTO.getBillingAmount());
        project.setBillingDescription(saveProjectDTO.getBillingDescription());
        project.setExpiration(saveProjectDTO.getExpiration());
        project.getHint().setBody(saveProjectDTO.getHintBody());

        return this.projectRepository.save(project);
    }

    public void delete(Integer projectId) {
        this.projectRepository.deleteById(projectId);
    }

    @Transactional
    public Project setStatusCommission(Integer projectId, Integer commissionId, String status) {
        Project project = this.findById(projectId);
        ProjectCommission projectCommission = project.getProjectCommissions().stream().filter(pc -> pc.getId().equals(commissionId))
                .findFirst().orElseThrow();

        if(projectCommission.getStatus().equals(ProjectCommissionStatus.valueOf(status))){
            return project;
        }

        projectCommission.setStatus(ProjectCommissionStatus.valueOf(status));
        projectCommission.getProjectStatusChanges().add(ProjectStatusChange.builder()
                .projectCommissionStatus(projectCommission.getStatus())
                .projectCommission(projectCommission)
                .build());

        if (project.getProjectCommissions().stream().allMatch(pc -> pc.getStatus() == ProjectCommissionStatus.SENT_TO_ADMINISTRATION)) {
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
                case INTERNAL_NETWORK:
                    predicateList.add(criteriaBuilder.isNotNull(root.get("domain")));
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
        }, pageable).map(Project::toListElement);
    }

    @Transactional
    public void setBulkStatusCommission(Integer id, String status, UpdateBulkProjectCommissionStatus updateBulkProjectCommissionStatus) {
        updateBulkProjectCommissionStatus.getIds().forEach(idCommission -> this.setStatusCommission(id, idCommission, status));
    }

    public void createMissingObjects() {
        log.info("Checking project without content");
        Consumer<ProjectCommission> pcConsumer = projectCommission -> this.contentRepository.save(
                Content.builder()
                        .contentStatus(ContentStatus.WORKING)
                        .projectCommission(projectCommission)
                        .build()
        );
        Page<ProjectCommission> pcSlice = this.projectCommissionRepository.findByContentIsNull(PageRequest.of(0, 10));
        pcSlice.forEach(pcConsumer);
        while (pcSlice.hasNext()) {
            log.info(pcSlice.getPageable().getPageNumber() + " page executed");
            pcSlice = this.projectCommissionRepository.findByContentIsNull(pcSlice.nextPageable());
            pcSlice.forEach(pcConsumer);
        }

        //######

        log.info("Checking content without hint");
        Consumer<Content> cConsumer = content -> {
            content.setHint(ContentHint
                    .builder()
                    .build());
            this.contentRepository.save(content);
        };
        Page<Content> cSlice = this.contentRepository.findByHintIsNull(PageRequest.of(0, 10));
        cSlice.forEach(cConsumer);
        while (cSlice.hasNext()) {
            log.info(cSlice.getPageable().getPageNumber() + " page executed");
            cSlice = this.contentRepository.findByHintIsNull(cSlice.nextPageable());
            cSlice.forEach(cConsumer);

        }

        //##########

        log.info("Checking project without hint");
        Consumer<Project> pConsumer = project -> {
            project.setHint(ContentHint
                    .builder()
                    .project(project)
                    .build());
            this.projectRepository.save(project);
        };
        Page<Project> pSlice = this.projectRepository.findByHintIsNull(PageRequest.of(0, 10));
        pSlice.forEach(pConsumer);
        while (pSlice.hasNext()) {
            log.info(pSlice.getPageable().getPageNumber() + " page executed");
            pSlice = this.projectRepository.findByHintIsNull(pSlice.nextPageable());
            pSlice.forEach(pConsumer);
        }

        log.info("Done!");
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

    public ProjectCommission findByProjectIdAndProjectCommission(Integer id, Integer idCommission) {
        Project project = this.findById(id);
        return project.getProjectCommissions().stream().filter(element -> element.getId().equals(idCommission)).findFirst().orElseThrow();
    }

    public void updateProjectCommissionHint(Integer id, Integer idCommission, SaveProjectCommissionHintDTO saveProjectCommissionHintDTO) {
        ProjectCommission projectCommission = this.findByProjectIdAndProjectCommission(id, idCommission);
        ContentHint hint = projectCommission.getContent().getHint();
        hint.setBody(saveProjectCommissionHintDTO.getBody());
        this.contentHintRepository.save(hint);
    }


    public void updateProjectHint(Integer id, SaveProjectCommissionHintDTO saveProjectCommissionHintDTO) {
        Project project = this.findById(id);
        ContentHint hint = project.getHint();
        hint.setBody(saveProjectCommissionHintDTO.getBody());
        this.contentHintRepository.save(hint);
    }

    public void addProjectCommissionHintAttachment(Integer id, Integer idCommission, SaveAttachmentDTO saveAttachmentDTO) {
        ProjectCommission projectCommission = this.findByProjectIdAndProjectCommission(id, idCommission);
        this.attachmentRepository.save(Attachment
                .builder()
                .contentHint(projectCommission.getContent().getHint())
                .filename(saveAttachmentDTO.getFilename())
                .payload(Base64.getDecoder().decode(saveAttachmentDTO.getBody()))
                .build()
        );
    }

    public void deleteProjectCommissionHintAttachment(Integer id, Integer idCommission, Integer idAttachment) {
        ProjectCommission projectCommission = this.findByProjectIdAndProjectCommission(id, idCommission);
        this.attachmentRepository.deleteByIdAndContentHint(idAttachment, projectCommission.getContent().getHint());
    }

    public void addProjectHintAttachment(Integer id, SaveAttachmentDTO saveAttachmentDTO) {
        Project project = this.findById(id);
        this.attachmentRepository.save(Attachment
                .builder()
                .contentHint(project.getHint())
                .filename(saveAttachmentDTO.getFilename())
                .payload(Base64.getDecoder().decode(saveAttachmentDTO.getBody()))
                .build()
        );
    }

    public void deleteProjectHintAttachment(Integer id, Integer idAttachment) {
        Project project = this.findById(id);
        this.attachmentRepository.deleteByIdAndContentHint(idAttachment, project.getHint());
    }

    public Project findByIdForDetail(Integer id) {
        Project project = this.findById(id);
        project.setProjectCommissions(null);
        return project;
    }

    public List<ProjectCommission> findProjectCommissions(Integer id, Sort sort) {
        Project project = this.findById(id);
        return this.projectCommissionRepository.findByProject(project, sort);
    }
}
