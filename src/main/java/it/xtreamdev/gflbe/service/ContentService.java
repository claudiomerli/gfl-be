package it.xtreamdev.gflbe.service;

import com.itextpdf.html2pdf.HtmlConverter;
import it.xtreamdev.gflbe.dto.SaveContentDTO;
import it.xtreamdev.gflbe.dto.SearchContentDTO;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import it.xtreamdev.gflbe.repository.*;
import it.xtreamdev.gflbe.security.JwtTokenUtil;
import it.xtreamdev.gflbe.security.model.JwtUserPrincipal;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ContentRulesRepository contentRulesRepository;
    @Autowired
    private NewspaperRepository newspaperRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private FtpService ftpService;
    @Autowired
    private ContentMailService contentMailService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public Long count() {
        return this.contentRepository.count();
    }

    public Page<Content> findAll(
            SearchContentDTO searchContentDTO,
            PageRequest pageRequest
    ) {
        return this.contentRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Content, Customer> customerJoin = root.join("customer");
            Join<Content, User> editorJoin = root.join("editor");
            Join<Content, Newspaper> newspaperJoin = root.join("newspaper");

            Optional.ofNullable(searchContentDTO.getGlobalSearch()).ifPresent(globalSearchValue -> predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("title")), "%" + globalSearchValue.toUpperCase() + "%")));
            Optional.ofNullable(searchContentDTO.getCustomerId()).ifPresent(customerIdValue -> predicates.add(criteriaBuilder.equal(customerJoin.get("id"), customerIdValue)));
            Optional.ofNullable(searchContentDTO.getEditorId()).ifPresent(editorIdValue -> predicates.add(criteriaBuilder.equal(editorJoin.get("id"), editorIdValue)));
            Optional.ofNullable(searchContentDTO.getNewspaperId()).ifPresent(newspaperIdValue -> predicates.add(criteriaBuilder.equal(newspaperJoin.get("id"), newspaperIdValue)));
            Optional.ofNullable(searchContentDTO.getStatus()).ifPresent(contentStatusValue -> predicates.add(criteriaBuilder.equal(root.get("contentStatus"), contentStatusValue)));

            Optional.ofNullable(searchContentDTO.getCreatedDateFrom()).ifPresent(date -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), date)));
            Optional.ofNullable(searchContentDTO.getCreatedDateTo()).ifPresent(date -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), date)));
            Optional.ofNullable(searchContentDTO.getDeliveryDateFrom()).ifPresent(date -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("deliveryDate"), date)));
            Optional.ofNullable(searchContentDTO.getDeliveryDateTo()).ifPresent(date -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("deliveryDate"), date)));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, pageRequest);
    }

    public void save(SaveContentDTO saveContentDTO) {
        Content content = saveContentDTO.getContent();
        content.setContentStatus(ContentStatus.WORKING);

        User editor = this.userRepository.findById(saveContentDTO.getEditorId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Editor id not found"));
        Project project = this.projectRepository.findById(saveContentDTO.getProjectId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Customer id not found"));
        Newspaper newspaper = this.newspaperRepository.findById(saveContentDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Newspaper id not found"));

        content.setEditor(editor);
        content.setProject(project);
        content.setNewspaper(newspaper);

        this.contentRulesRepository.save(content.getContentRules());
        this.contentRepository.save(content);
        content.setCustomerToken(this.jwtTokenUtil.createJwtCustomerCodeFromContentId(content.getId()));
        this.contentRepository.save(content);
        this.contentMailService.sendCreationMail(content);
    }

    public SaveContentDTO loadSaveContentDto(Integer contentId) {
        Content content = this.contentRepository.findById(contentId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Content id not found"));
        return SaveContentDTO
                .builder()
                .content(content)
                .newspaperId(content.getNewspaper().getId())
                .projectId(content.getProject().getId())
                .editorId(content.getEditor().getId())
                .build();
    }

    public void update(Integer contentId, SaveContentDTO saveContentDTO) {
        Content contentToUpdate = this.contentRepository.findById(contentId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Content id not found"));
        Content contentUpdated = saveContentDTO.getContent();
        boolean statusUpdated = !contentUpdated.getContentStatus().equals(contentToUpdate.getContentStatus());

        User editor = this.userRepository.findById(saveContentDTO.getEditorId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Editor id not found"));
        Project project = this.projectRepository.findById(saveContentDTO.getProjectId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Customer id not found"));
        Newspaper newspaper = this.newspaperRepository.findById(saveContentDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Newspaper id not found"));

        contentToUpdate.setContentRules(contentUpdated.getContentRules());
        contentToUpdate.setContentStatus(contentUpdated.getContentStatus());
        contentToUpdate.setBody(contentUpdated.getBody());
        contentToUpdate.setTitle(contentUpdated.getTitle());
        contentToUpdate.setLinkText(contentUpdated.getLinkText());
        contentToUpdate.setLinkUrl(contentUpdated.getLinkUrl());
        contentToUpdate.setDeliveryDate(contentUpdated.getDeliveryDate());
        contentToUpdate.setScore(contentUpdated.getScore());
        contentToUpdate.setAdminNotes(contentUpdated.getAdminNotes());

        contentToUpdate.setEditor(editor);
        contentToUpdate.setProject(project);
        contentToUpdate.setNewspaper(newspaper);

        this.contentRulesRepository.save(contentToUpdate.getContentRules());
        this.contentRepository.save(contentToUpdate);

        if (statusUpdated) {
            this.contentMailService.sendUpdateMail(contentToUpdate);
        }
    }

    public void delete(Integer contentId) {
        this.contentRepository.deleteById(contentId);
    }

    public Page<Content> loadCurrentEditorContents(PageRequest pageRequest) {
        User user = this.userService.currentUserAuthentication();
        return this.contentRepository.findByEditorOrderByDeliveryDateDesc(user, pageRequest);
    }

    public Content loadContentByIdAndUser(Integer contentId, JwtUserPrincipal userPrincipal) {
        return this.contentRepository.findByIdAndEditor(contentId, userPrincipal.getUser()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot access to content"));
    }

    public void update(Integer contentId, JwtUserPrincipal principal, Content content) {
        Content contentToUpdate = this.loadContentByIdAndUser(contentId, principal);
        boolean statusUpdated = !contentToUpdate.getContentStatus().equals(content.getContentStatus());

        contentToUpdate.setTitle(content.getTitle());
        contentToUpdate.setLinkText(content.getLinkText());
        contentToUpdate.setLinkUrl(content.getLinkUrl());
        contentToUpdate.setBody(content.getBody());

        if (content.getContentStatus() != ContentStatus.WORKING && content.getContentStatus() != ContentStatus.DELIVERED) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Status not valid");
        } else {
            contentToUpdate.setContentStatus(content.getContentStatus());
        }

        this.contentMailService.sendCreationMail(content);
        this.contentRepository.save(contentToUpdate);

        if (statusUpdated) {
            this.contentMailService.sendUpdateMail(contentToUpdate);
        }
    }

    public Content loadByIdAndToken(Integer id, String token) {
        this.jwtTokenUtil.verifyCustomerJwt(token, id);
        return this.contentRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
    }

    public void approveContent(Integer id, String token) {
        this.jwtTokenUtil.verifyCustomerJwt(token, id);
        Content content = this.contentRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
        content.setContentStatus(ContentStatus.APPROVED);
        this.contentRepository.save(content);
        this.contentMailService.sendUpdateMail(content);
    }

    public byte[] exportDocx(Integer contentId) {
        Content content = this.contentRepository.findById(contentId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);

            wordMLPackage
                    .getMainDocumentPart()
                    .getContent()
                    .addAll(XHTMLImporter.convert(content
                            .getBody()
                            .replace("&nbsp;", " ")
                            .replace("&ograve;", "ò")
                            .replace("&agrave;", "à")
                            .replace("&eacute;", "é")
                            .replace("&egrave;", "è")
                            .replace("&ugrave;", "ù")
                            .replace("&igrave;", "ì"), null));

            wordMLPackage.save(baos);
            this.ftpService.storeFile(String.format("%s.docx", content.getTitle()), content.getProject().getCustomer().getName(), baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Error exporting file");
        }
    }

    public byte[] exportPdf(Integer contentId) {
        Content content = this.contentRepository.findById(contentId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            HtmlConverter.convertToPdf(content.getBody(), baos);

            this.ftpService.storeFile(String.format("%s.pdf", content.getTitle()), content.getProject().getCustomer().getName(), baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Error exporting file");
        }
    }

    public void saveNotesToContent(Integer id, String notes, String token) {
        this.jwtTokenUtil.verifyCustomerJwt(token, id);

        Content content = this.contentRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
        content.setCustomerNotes(notes);
        this.contentRepository.save(content);
    }
}
