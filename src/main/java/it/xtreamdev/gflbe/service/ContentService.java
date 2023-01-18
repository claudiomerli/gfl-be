package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.content.FindContentFilterDTO;
import it.xtreamdev.gflbe.dto.content.SaveContentDTO;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import it.xtreamdev.gflbe.model.enumerations.ProjectCommissionStatus;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.ContentRepository;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static it.xtreamdev.gflbe.util.DocxUtils.substituteDocxSpecialCharacters;


@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserService userService;

    public Page<Content> findAll(FindContentFilterDTO findContentFilterDTO, PageRequest pageRequest) {
        User user = userService.userInfo();
        return this.contentRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Content, ProjectCommission> projectCommission = root.join("projectCommission");
            Join<ProjectCommission, Project> project = projectCommission.join("project");
            Join<ProjectCommission, Newspaper> newspaper = projectCommission.join("newspaper");

            if (user.getRole() == RoleName.EDITOR) {
                predicates.add(criteriaBuilder.equal(root.get("editor"), user));
            }

            if (user.getRole() == RoleName.CUSTOMER) {
                predicates.add(criteriaBuilder.equal(project.get("customer"), user));
                predicates.add(root.get("contentStatus").in(ContentStatus.SENT_TO_CUSTOMER, ContentStatus.APPROVED));
            }

            if (user.getRole() == RoleName.PUBLISHER) {
                predicates.add(projectCommission.get("status").in(ProjectCommissionStatus.TO_PUBLISH, ProjectCommissionStatus.STANDBY_PUBLICATION, ProjectCommissionStatus.SENT_TO_NEWSPAPER, ProjectCommissionStatus.SENT_TO_ADMINISTRATION));
            }

            if (StringUtils.isNotBlank(findContentFilterDTO.getContentStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("contentStatus"), ContentStatus.valueOf(findContentFilterDTO.getContentStatus())));
            }

            if (StringUtils.isNotBlank(findContentFilterDTO.getProjectId())) {
                predicates.add(criteriaBuilder.equal(project.get("id"), Integer.valueOf(findContentFilterDTO.getProjectId())));
            }

            if (StringUtils.isNotBlank(findContentFilterDTO.getNewspaperId())) {
                predicates.add(criteriaBuilder.equal(newspaper.get("id"), Integer.valueOf(findContentFilterDTO.getNewspaperId())));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, pageRequest);
    }

    public Content findById(Integer id) {
        User user = this.userService.userInfo();
        Content content = this.contentRepository.findById(id).orElseThrow();

        if (user.getRole() == RoleName.CUSTOMER && !content.getProjectCommission().getProject().getCustomer().getId().equals(user.getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Not allowed to view this content");
        }

        if (user.getRole() == RoleName.EDITOR && !content.getEditor().getId().equals(user.getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Not allowed to view this content");
        }

        return content;
    }

    public void update(Integer id, SaveContentDTO saveContentDTO) {
        Content content = this.findById(id);
        content.setBody(saveContentDTO.getBody());
        this.contentRepository.save(content);
    }

    public void changeStatus(Integer id, ContentStatus contentStatus) {
        Content content = this.findById(id);
        content.setContentStatus(contentStatus);
        this.contentRepository.save(content);
    }

    public void assignToEditor(Integer id, Integer editorId) {
        Content content = this.findById(id);
        User user = this.userService.findById(editorId);
        content.setEditor(user);
        this.contentRepository.save(content);
    }

    public byte[] exportDocx(Integer id) {
        Content content = this.findById(id);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
            wordMLPackage.getMainDocumentPart().getContent().addAll(XHTMLImporter.convert(substituteDocxSpecialCharacters("<div>" + content.getBody() + "</div>"), null));
            wordMLPackage.save(baos);
            return baos.toByteArray();
        } catch (IOException | Docx4JException e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error exporting file");
        }
    }
}
