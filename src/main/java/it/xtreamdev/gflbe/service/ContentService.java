package it.xtreamdev.gflbe.service;

import com.itextpdf.html2pdf.HtmlConverter;
import it.xtreamdev.gflbe.dto.SaveContentDTO;
import it.xtreamdev.gflbe.dto.SearchContentDTO;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.*;
import it.xtreamdev.gflbe.security.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private ContentLinkRepository contentLinkRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ContentRulesRepository contentRulesRepository;
    @Autowired
    private NewspaperRepository newspaperRepository;
    @Autowired
    private CustomerRepository customerRepository;

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
        User user = this.userService.userInfo();
        if (user.getRole() == RoleName.ADMIN) {
            return this.findAllAdmin(searchContentDTO, pageRequest);
        }

        if (user.getRole() == RoleName.EDITOR) {
            return this.findAllEditor(user, searchContentDTO, pageRequest);
        }

        throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Role not found");
    }

    private Page<Content> findAllAdmin(SearchContentDTO searchContentDTO,
                                       PageRequest pageRequest) {
        return this.contentRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Content, Customer> customerJoin = root.join("customer");
            Join<Content, User> editorJoin = root.join("editor");
            Join<Content, Newspaper> newspaperJoin = root.join("newspaper");
            Join<Content, Project> projectJoin = root.join("project");

            Optional.ofNullable(searchContentDTO.getMonthUse()).ifPresent(monthUse -> predicates.add(criteriaBuilder.equal(root.get("monthUse"), monthUse)));

            Optional.ofNullable(searchContentDTO.getCustomerId()).ifPresent(customerIdValue -> predicates.add(criteriaBuilder.equal(customerJoin.get("id"), customerIdValue)));
            Optional.ofNullable(searchContentDTO.getEditorId()).ifPresent(editorIdValue -> predicates.add(criteriaBuilder.equal(editorJoin.get("id"), editorIdValue)));
            Optional.ofNullable(searchContentDTO.getNewspaperId()).ifPresent(newspaperIdValue -> predicates.add(criteriaBuilder.equal(newspaperJoin.get("id"), newspaperIdValue)));
            Optional.ofNullable(searchContentDTO.getProjectId()).ifPresent(projectIdValue -> predicates.add(criteriaBuilder.equal(projectJoin.get("id"), projectIdValue)));

            return getPredicateForCommonParameter(searchContentDTO, root, criteriaBuilder, predicates);
        }, pageRequest);
    }

    private Predicate getPredicateForCommonParameter(SearchContentDTO searchContentDTO, Root<Content> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        Optional.ofNullable(searchContentDTO.getStatus()).ifPresent(contentStatusValue -> predicates.add(criteriaBuilder.equal(root.get("contentStatus"), contentStatusValue)));
        Optional.ofNullable(searchContentDTO.getGlobalSearch()).ifPresent(globalSearchValue -> predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("title")), "%" + globalSearchValue.toUpperCase() + "%")));
        Optional.ofNullable(searchContentDTO.getCreatedDateFrom()).ifPresent(date -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), date)));
        Optional.ofNullable(searchContentDTO.getCreatedDateTo()).ifPresent(date -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), date)));
        Optional.ofNullable(searchContentDTO.getDeliveryDateFrom()).ifPresent(date -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("deliveryDate"), date)));
        Optional.ofNullable(searchContentDTO.getDeliveryDateTo()).ifPresent(date -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("deliveryDate"), date)));

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    private Page<Content> findAllEditor(User user, SearchContentDTO searchContentDTO, PageRequest pageRequest) {
        return this.contentRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Content, User> editorJoin = root.join("editor");
            predicates.add(criteriaBuilder.equal(editorJoin.get("id"), user.getId()));

            return getPredicateForCommonParameter(searchContentDTO, root, criteriaBuilder, predicates);
        }, pageRequest);
    }

    @Transactional
    public void save(SaveContentDTO saveContentDTO) {
        Content content = Content.builder().build();
        content.setContentStatus(ContentStatus.WORKING);

        User editor = this.userRepository.findById(saveContentDTO.getEditorId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Editor id not found"));
        Newspaper newspaper = this.newspaperRepository.findById(saveContentDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Newspaper id not found"));
        Customer customer = this.customerRepository.findById(saveContentDTO.getCustomerId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Customer id not found"));

        content.setTitle(saveContentDTO.getTitle());
        content.setLinks(
                saveContentDTO.getLinks().stream()
                        .map(link -> ContentLink.builder()
                                .linkText(link.getLinkText())
                                .linkUrl(link.getLinkUrl())
                                .content(content)
                                .build()
                        )
                        .collect(Collectors.toList())
        );
        content.setBody(saveContentDTO.getBody());
        content.setDeliveryDate(saveContentDTO.getDeliveryDate());

        content.setEditor(editor);
        content.setNewspaper(newspaper);
        content.setCustomer(customer);
        content.setCustomerToken(this.jwtTokenUtil.createJwtCustomerCodeFromContentId());
        content.setMonthUse(saveContentDTO.getMonthUse());
        content.setContentRules(ContentRules.builder()
                .maxCharacterBodyLength(saveContentDTO.getContentRules().getMaxCharacterBodyLength())
                .title(saveContentDTO.getContentRules().getTitle())
                .body(saveContentDTO.getContentRules().getBody())
                .linkText(saveContentDTO.getContentRules().getLinkText())
                .linkUrl(saveContentDTO.getContentRules().getLinkUrl())
                .build()
        );

        content.getContentRules()
                .setLinks(saveContentDTO.getContentRules().getLinks().stream()
                        .map(link -> ContentLink.builder()
                                .linkUrl(link.getLinkUrl())
                                .contentRules(content.getContentRules())
                                .build()
                        )
                        .collect(Collectors.toList()));

        if (Objects.nonNull(saveContentDTO.getContentRules().getAttachmentBase64())) {
            content.getContentRules().setAttachment(Attachment.builder()
                    .contentType(saveContentDTO.getContentRules().getAttachmentContentType())
                    .filename(saveContentDTO.getContentRules().getAttachmentFileName())
                    .attachmentData(AttachmentData.builder().bytes(Base64.getDecoder().decode(saveContentDTO.getContentRules().getAttachmentBase64())).build())
                    .build());
        }

        this.contentRepository.save(content);
        this.contentMailService.sendCreationMail(content);
    }

    @Transactional
    public void update(Integer contentId, SaveContentDTO saveContentDTO) {
        User user = this.userService.userInfo();
        if (user.getRole() == RoleName.ADMIN) {
            this.updateForAdmin(contentId, saveContentDTO);
        } else if (user.getRole() == RoleName.EDITOR) {
            this.updateForEditor(user, contentId, saveContentDTO);
        } else {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Role not found");
        }
    }

    @Transactional
    public void updateForAdmin(Integer contentId, SaveContentDTO saveContentDTO) {
        Content contentToUpdate = this.contentRepository.findById(contentId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Content id not found"));
        ContentStatus previousContentStatus = contentToUpdate.getContentStatus();

        User editor = this.userRepository.findById(saveContentDTO.getEditorId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Editor id not found"));
        Newspaper newspaper = this.newspaperRepository.findById(saveContentDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Newspaper id not found"));
        Customer customer = this.customerRepository.findById(saveContentDTO.getCustomerId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Customer id not found"));

        this.contentLinkRepository.deleteByContentRules(contentToUpdate.getContentRules());
        contentToUpdate.getContentRules().setBody(saveContentDTO.getContentRules().getBody());
        contentToUpdate.getContentRules().setTitle(saveContentDTO.getContentRules().getTitle());
        contentToUpdate.getContentRules().setLinkText(saveContentDTO.getContentRules().getLinkText());
        contentToUpdate.getContentRules().setLinkUrl(saveContentDTO.getContentRules().getLinkUrl());
        contentToUpdate.getContentRules().setMaxCharacterBodyLength(saveContentDTO.getContentRules().getMaxCharacterBodyLength());
        contentToUpdate.getContentRules().setLinks(saveContentDTO.getContentRules().getLinks().stream()
                .map(link -> this.contentLinkRepository.save(
                        ContentLink.builder()
                                .linkUrl(link.getLinkUrl())
                                .contentRules(contentToUpdate.getContentRules())
                                .build()
                ))
                .collect(Collectors.toList()));

        if (Objects.nonNull(saveContentDTO.getContentRules().getAttachmentBase64())) {
            contentToUpdate.getContentRules().setAttachment(Attachment.builder()
                    .contentType(saveContentDTO.getContentRules().getAttachmentContentType())
                    .filename(saveContentDTO.getContentRules().getAttachmentFileName())
                    .attachmentData(AttachmentData.builder().bytes(Base64.getDecoder().decode(saveContentDTO.getContentRules().getAttachmentBase64())).build())
                    .build());
        } else if (Objects.isNull(saveContentDTO.getContentRules().getAttachmentFileName())) {
            contentToUpdate.getContentRules().setAttachment(null);
        }

        contentToUpdate.setContentStatus(saveContentDTO.getContentStatus());
        contentToUpdate.setBody(saveContentDTO.getBody());
        contentToUpdate.setTitle(saveContentDTO.getTitle());
        this.contentLinkRepository.deleteByContent(contentToUpdate);
        contentToUpdate.setLinks(
                saveContentDTO.getLinks().stream()
                        .map(link -> this.contentLinkRepository.save(
                                ContentLink.builder()
                                        .linkText(link.getLinkText())
                                        .linkUrl(link.getLinkUrl())
                                        .content(contentToUpdate)
                                        .build()
                        ))
                        .collect(Collectors.toList())
        );
        contentToUpdate.setDeliveryDate(saveContentDTO.getDeliveryDate());
        contentToUpdate.setScore(saveContentDTO.getScore());
        contentToUpdate.setAdminNotes(saveContentDTO.getAdminNotes());
        contentToUpdate.setMonthUse(saveContentDTO.getMonthUse());

        contentToUpdate.setEditor(editor);
        contentToUpdate.setNewspaper(newspaper);
        contentToUpdate.setCustomer(customer);

        this.contentRulesRepository.save(contentToUpdate.getContentRules());
        this.contentRepository.save(contentToUpdate);

        if (!previousContentStatus.equals(saveContentDTO.getContentStatus())) {
            this.contentMailService.sendUpdateMail(contentToUpdate);
        }
    }

    @Transactional
    public void updateForEditor(User editor, Integer contentId, SaveContentDTO saveContentDTO) {
        Content contentToUpdate = this.contentRepository.findById(contentId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Content id not found"));
        if (!contentToUpdate.getEditor().getId().equals(editor.getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Editor not associated to content");
        }

        if (contentToUpdate.getContentStatus() != ContentStatus.WORKING) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "The content is not in WORKING status");
        }

        contentToUpdate.setBody(saveContentDTO.getBody());
        contentToUpdate.setTitle(saveContentDTO.getTitle());

        this.contentLinkRepository.deleteByContent(contentToUpdate);
        contentToUpdate.setLinks(
                saveContentDTO.getLinks().stream()
                        .map(link -> this.contentLinkRepository.save(
                                ContentLink.builder()
                                        .linkText(link.getLinkText())
                                        .linkUrl(link.getLinkUrl())
                                        .content(contentToUpdate)
                                        .build()
                        ))
                        .collect(Collectors.toList())
        );

        this.contentRepository.save(contentToUpdate);
    }

    @Transactional
    public void delete(Integer contentId) {
        this.contentRepository.deleteById(contentId);
    }

    public Content findById(Integer id) {
        User user = this.userService.userInfo();
        Content content = this.contentRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Content id not found"));

        if (user.getRole() == RoleName.EDITOR && !user.getId().equals(content.getEditor().getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Editor not associated to content");
        }

        return content;
    }

    public Content loadByIdAndToken(Integer id, String token) {
        Content content = this.contentRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
        this.jwtTokenUtil.verifyCustomerJwt(token, content);
        return content;
    }

    public void deliverContent(Integer id) {
        User user = this.userService.userInfo();
        Content content = this.contentRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));

        if (user.getRole() == RoleName.EDITOR && !user.getId().equals(content.getEditor().getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Editor not associated to content");
        }

        content.setContentStatus(ContentStatus.DELIVERED);

        this.contentRepository.save(content);
        this.contentMailService.sendUpdateMail(content);
    }

    public void approveContent(Integer id, String token) {
        Content content = this.contentRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
        this.jwtTokenUtil.verifyCustomerJwt(token, content);

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
                    .addAll(XHTMLImporter.convert(
                            "<div>" + content
                                    .getBody()
                                    .replace("&nbsp;", " ")
                                    .replace("&ograve;", "ò")
                                    .replace("&agrave;", "à")
                                    .replace("&eacute;", "é")
                                    .replace("&egrave;", "è")
                                    .replace("&ugrave;", "ù")
                                    .replace("&igrave;", "ì")
                                    .replace("&Egrave;", "È")
                                    .replace("&Eacute;", "É")
                                    .replace("&rsquo;", "’")
                                    .replace("&lsquo;", "‘") +
                                    "</div>", null));

            wordMLPackage.save(baos);
            this.ftpService.storeFile(String.format("%s.docx", content.getTitle()), content.getCustomer().getName(), baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting file", e);
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Error exporting file");
        }
    }

    public byte[] exportPdf(Integer contentId) {
        Content content = this.contentRepository.findById(contentId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            HtmlConverter.convertToPdf(content.getBody(), baos);

            this.ftpService.storeFile(String.format("%s.pdf", content.getTitle()), content.getCustomer().getName(), baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting file", e);
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Error exporting file");
        }
    }

    public void saveNotesToContent(Integer id, String notes, String token) {
        Content content = this.contentRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
        this.jwtTokenUtil.verifyCustomerJwt(token, content);

        content.setCustomerNotes(notes);

        this.contentRepository.save(content);
    }
}
