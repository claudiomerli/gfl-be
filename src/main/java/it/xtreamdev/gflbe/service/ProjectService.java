package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.content.SaveAttachmentDTO;
import it.xtreamdev.gflbe.dto.content.SaveProjectCommissionHintDTO;
import it.xtreamdev.gflbe.dto.majestic.LinkCheckDTO;
import it.xtreamdev.gflbe.dto.majestic.SecondLevelCheckDTO;
import it.xtreamdev.gflbe.dto.newspaper.NewspaperDTO;
import it.xtreamdev.gflbe.dto.project.*;
import it.xtreamdev.gflbe.mapper.NewspaperMapper;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import it.xtreamdev.gflbe.model.enumerations.ProjectCommissionStatus;
import it.xtreamdev.gflbe.model.enumerations.ProjectStatus;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.xlsx4j.sml.SheetData;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    private ContentPurchaseService contentPurchaseService;
    @Autowired
    private ProjectLinkRepository projectLinkRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NewspaperService newspaperService;
    @Autowired
    private NewspaperMapper newspaperMapper;
    @Autowired
    private MajesticSEOService majesticSEOService;

    @Autowired
    private RestTemplate restTemplate;


    public Project findById(Integer id) {
        User user = userService.userInfo();
        Project project = this.projectRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
        if (user.getRole().equals(RoleName.CUSTOMER) && !user.getId().equals(project.getCustomer().getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "User not allowed to view this project");
        }

        return project;
    }

    public void createProjectFromDomain(Domain domain) {
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
    public void removeReferenceFromDomain(Domain domain) {
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
                .newspaper(saveProjectCommissionDTO.getNewspaperId() != null ? this.newspaperService.findById(saveProjectCommissionDTO.getNewspaperId()) : null)
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
        project.setCustomer(saveProjectDTO.getCustomerId() != null ? userService.findById(saveProjectDTO.getCustomerId()) : null);
        project.setBillingAmount(saveProjectDTO.getBillingAmount());
        project.setBillingDescription(saveProjectDTO.getBillingDescription());
        project.setExpiration(saveProjectDTO.getExpiration());
        project.getHint().setBody(saveProjectDTO.getHintBody());

        return this.projectRepository.save(project);
    }

    @Transactional
    public void delete(Integer projectId) {
        Project project = this.findById(projectId);
        project.setDeleted(true);
        this.projectRepository.save(project);
    }

    @Transactional
    public Project setStatusCommission(Integer projectId, Integer commissionId, String status) {
        return this.setStatusCommission(projectId, commissionId, status, null);
    }

    @Transactional
    public Project setStatusCommission(Integer projectId, Integer commissionId, String status, Map<String, String> metadata) {
        Project project = this.findById(projectId);
        ProjectCommission projectCommission = project.getProjectCommissions().stream().filter(pc -> pc.getId().equals(commissionId))
                .findFirst().orElseThrow();

        if (projectCommission.getStatus().equals(ProjectCommissionStatus.valueOf(status))) {
            return project;
        }


        projectCommission.setStatus(ProjectCommissionStatus.valueOf(status));
        projectCommission.getProjectStatusChanges().add(ProjectStatusChange.builder()
                .projectCommissionStatus(projectCommission.getStatus())
                .projectCommission(projectCommission)
                .build());

        if (ProjectCommissionStatus.valueOf(status) == ProjectCommissionStatus.SENT_TO_ADMINISTRATION) {
            String contentPurchasedId = metadata.get("contentPurchasedId");
            ContentPurchase contentPurchase = this.contentPurchaseService.findById(Integer.valueOf(contentPurchasedId));
            projectCommission.setContentPurchase(contentPurchase);
        }

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

    public Page<ProjectListElementDTO> find(SearchProjectDTO searchProjectDTO, Pageable pageable) {
        User user = this.userService.userInfo();
        return this.projectRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            Join<Project, ProjectCommission> projectCommissionsJoin = root.join("projectCommissions", JoinType.LEFT);
            Join<Project, User> customerJoin = root.join("customer", JoinType.LEFT);
            criteriaQuery.distinct(true);


            if (StringUtils.isNotBlank(searchProjectDTO.getStatus())) {
                predicateList.add(criteriaBuilder.equal(root.get("status"), ProjectStatus.valueOf(searchProjectDTO.getStatus())));
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
                case FINAL_CUSTOMER:
                    predicateList.add(criteriaBuilder.isMember(user, root.get("finalCustomers")));
                    break;
                case INTERNAL_NETWORK:
                    predicateList.add(criteriaBuilder.isNotNull(root.get("domain")));
            }

            if (StringUtils.isNotBlank(searchProjectDTO.getGlobalSearch())) {
                Arrays.stream(searchProjectDTO.getGlobalSearch().split(" ")).forEach(s -> {
                    predicateList.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + s.toUpperCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(customerJoin.get("fullname")), "%" + s.toUpperCase() + "%")
                    ));
                });
            }

            if (searchProjectDTO.getProjectCommissionStatus() != null &&
                    !searchProjectDTO.getProjectCommissionStatus().isEmpty()) {
                predicateList.add(criteriaBuilder.or(searchProjectDTO.getProjectCommissionStatus().stream().map(statusFilter -> criteriaBuilder.equal(projectCommissionsJoin.get("status"), ProjectCommissionStatus.valueOf(statusFilter))).toArray(Predicate[]::new)));
            }


            if (StringUtils.isNotBlank(searchProjectDTO.getCommissionPeriod())) {
                predicateList.add(criteriaBuilder.equal(projectCommissionsJoin.get("period"), Month.valueOf(searchProjectDTO.getCommissionPeriod())));
            }

            if (searchProjectDTO.getCommissionYear() != null) {
                predicateList.add(criteriaBuilder.equal(projectCommissionsJoin.get("year"), searchProjectDTO.getCommissionYear()));
            }

            if (searchProjectDTO.getNewspapers() != null && !searchProjectDTO.getNewspapers().isEmpty()) {
                predicateList.add(criteriaBuilder.or(searchProjectDTO.getNewspapers().stream().map(idNewspaper -> {
                    Newspaper newspaper = this.newspaperService.findById(idNewspaper);
                    return criteriaBuilder.equal(projectCommissionsJoin.get("newspaper"), newspaper);
                }).collect(Collectors.toList()).toArray(Predicate[]::new)));
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

    public byte[] exportProjectExcel(Integer id, Sort sort) {
        Project project = this.findById(id);
        List<ProjectCommission> projectCommissions = this.findProjectCommissions(id, sort);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            SpreadsheetMLPackage spreadsheet = createSpreadsheet();
            SheetData sheet = addSheet(spreadsheet, project.getName());
            addRow(sheet, "Testata", "Url di pubblicazione", "Data di pubblicazione", "Periodo");
            projectCommissions.forEach(projectCommission -> addRow(sheet,
                    projectCommission.getNewspaper() != null ? projectCommission.getNewspaper().getName() : null,
                    projectCommission.getPublicationUrl() != null ? projectCommission.getPublicationUrl() : null,
                    projectCommission.getPublicationDate() != null ? projectCommission.getPublicationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null,
                    projectCommission.getPeriod() != null ? projectCommission.getPeriod().getDisplayName(TextStyle.FULL, Locale.ITALY) : null));
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

    public NewspaperDTO findNewspaperForDomainProject(Integer projectId) {
        Project project = this.findById(projectId);
        if (project.getIsDomainProject()) {
            return this.newspaperMapper.mapEntityToDTO(project.getDomain().getNewspaper());
        } else {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "The project is not a domain project");
        }
    }

    @Transactional
    public void assignFinalCustomer(Integer id, List<Integer> ids) {
        Project project = this.findById(id);
        List<User> finalCustomers = ids.stream().map(userId -> this.userService.findById(userId)).collect(Collectors.toList());
        project.getFinalCustomers().clear();
        project.getFinalCustomers().addAll(finalCustomers);
        this.projectRepository.save(project);
    }

    @Transactional
    public void setCommissionPublicationInfo(ProjectCommission projectCommission, LocalDateTime wordpressPublicationDate, String wordpressUrl) {
        projectCommission.setPublicationDate(wordpressPublicationDate.toLocalDate());
        projectCommission.setPublicationUrl(wordpressUrl);
        this.projectCommissionRepository.save(projectCommission);
    }

    public void addLinkToProject(Integer id, SaveProjectLinkDTO saveProjectLinkDTO) {
        Project project = this.findById(id);
        project.getProjectLinks().add(
                ProjectLink.builder()
                        .project(project)
                        .newspaper(this.newspaperService.findById(saveProjectLinkDTO.getNewspaperId()))
                        .year(saveProjectLinkDTO.getYear())
                        .period(Month.valueOf(saveProjectLinkDTO.getPeriod()))
                        .anchor(saveProjectLinkDTO.getAnchor())
                        .url(saveProjectLinkDTO.getUrl())
                        .publicationUrl(saveProjectLinkDTO.getPublicationUrl())
                        .build()
        );
        this.projectRepository.save(project);
    }

    public void updateLinkToProject(Integer id, Integer idLink, SaveProjectLinkDTO saveProjectLinkDTO) {
        Project project = this.findById(id);
        project.getProjectLinks().stream().filter(projectLink -> projectLink.getId().equals(idLink)).findFirst().ifPresent(projectLink -> {
            projectLink.setNewspaper(this.newspaperService.findById(saveProjectLinkDTO.getNewspaperId()));
            projectLink.setYear(saveProjectLinkDTO.getYear());
            projectLink.setPeriod(Month.valueOf(saveProjectLinkDTO.getPeriod()));
            projectLink.setAnchor(saveProjectLinkDTO.getAnchor());
            projectLink.setUrl(saveProjectLinkDTO.getUrl());
            projectLink.setPublicationUrl(saveProjectLinkDTO.getPublicationUrl());
        });
        this.projectRepository.save(project);
    }

    public List<ProjectLink> getLinksOfProject(Integer id, Sort sort) {
        Project project = this.findById(id);
        return this.projectLinkRepository.findByProject(project, sort);
    }

    public void removeLink(Integer idProject, Integer idLink) {
        Project project = this.findById(idProject);
        project.getProjectLinks().removeIf(projectLink -> projectLink.getId().equals(idLink));
        this.projectRepository.save(project);
    }


    public byte[] exportProjectLinkAnalysis(Integer idProject) {
        Project project = this.findById(idProject);
        List<LinkCheckDTO> linkCheckDTOS = this.startProjectLinkAnalysis(project);
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(project.getName());
            Row headerRow = sheet.createRow(0);
            List<String> headerCells = List.of("Url", "Pagina", "Ancora", "Online", "Index", "Contiene link", "Contiene ancora", "Link follow");
            IntStream.range(0, headerCells.size())
                    .forEachOrdered(value -> {
                        Cell cell = headerRow.createCell(value);
                        cell.setCellValue(headerCells.get(value));
                    });

            IntStream.range(1, linkCheckDTOS.size() + 1).forEachOrdered(value -> {
                Row row = sheet.createRow(value);

                Cell cellUrl = row.createCell(0);
                cellUrl.setCellValue(linkCheckDTOS.get(value - 1).getUrl());

                Cell cellPublicationUrl = row.createCell(1);
                cellPublicationUrl.setCellValue(linkCheckDTOS.get(value - 1).getPublicationUrl());

                Cell cellAnchor = row.createCell(2);
                cellAnchor.setCellValue(linkCheckDTOS.get(value - 1).getAnchor());

                Cell cellIsOnline = row.createCell(3);
                cellIsOnline.setCellValue(linkCheckDTOS.get(value - 1).getIsOnline() ? "SI" : "NO");

                Cell cellIsIndex = row.createCell(4);
                cellIsIndex.setCellValue(linkCheckDTOS.get(value - 1).getIsInIndex() ? "SI" : "NO");

                Cell cellContainsUrl = row.createCell(5);
                cellContainsUrl.setCellValue(linkCheckDTOS.get(value - 1).getContainsUrl() ? "SI" : "NO");

                Cell cellContainsAnchor = row.createCell(6);
                cellContainsAnchor.setCellValue(linkCheckDTOS.get(value - 1).getContainsCorrectAnchorText() ? "SI" : "NO");

                Cell cellIsFollow = row.createCell(7);
                cellIsFollow.setCellValue(linkCheckDTOS.get(value - 1).getIsFollow() ? "SI" : "NO");
            });

            IntStream.range(0, 8).forEach(sheet::autoSizeColumn);
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<LinkCheckDTO> startProjectLinkAnalysis(Integer idProject) {
        Project project = this.findById(idProject);
        return startProjectLinkAnalysis(project);
    }

    public List<LinkCheckDTO> startProjectLinkAnalysis(Project project) {
        return Stream.concat(
                project.getProjectCommissions().stream()
                        .parallel()
                        .filter(projectCommission -> StringUtils.isNotBlank(projectCommission.getPublicationUrl()))
                        .map(projectCommission -> this.majesticSEOService.startAnalysisForLink(projectCommission.getPublicationUrl(), projectCommission.getUrl(), projectCommission.getAnchor())),
                project.getProjectLinks().stream()
                        .parallel()
                        .map(projectLink -> this.majesticSEOService.startAnalysisForLink(projectLink.getPublicationUrl(), projectLink.getUrl(), projectLink.getAnchor()))
        ).collect(Collectors.toList());
    }


}
