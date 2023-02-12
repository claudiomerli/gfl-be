package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.content.FindContentFilterDTO;
import it.xtreamdev.gflbe.dto.content.PublishOnWordpressDTO;
import it.xtreamdev.gflbe.dto.content.SaveContentDTO;
import it.xtreamdev.gflbe.dto.content.wordpress.WordpressBaseApi;
import it.xtreamdev.gflbe.dto.content.wordpress.categories.WordpressCategoryResponse;
import it.xtreamdev.gflbe.dto.content.wordpress.media.response.WordpressUploadMediaResponse;
import it.xtreamdev.gflbe.dto.content.wordpress.publication.WordpressCreatePostResponse;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import it.xtreamdev.gflbe.model.enumerations.ProjectCommissionStatus;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.ContentRepository;
import it.xtreamdev.gflbe.repository.ContentWordpressCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static it.xtreamdev.gflbe.util.DocxUtils.substituteDocxSpecialCharacters;


@Service
@Slf4j
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentWordpressCategoryRepository contentWordpressCategoryRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    public Page<Content> findAll(FindContentFilterDTO findContentFilterDTO, PageRequest pageRequest) {
        User user = userService.userInfo();
        return this.contentRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Content, ProjectCommission> projectCommission = root.join("projectCommission");
            Join<Content, User> editor = root.join("editor");
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
                predicates.add(projectCommission.get("status").in(ProjectCommissionStatus.TO_PUBLISH, ProjectCommissionStatus.STANDBY_PUBLICATION, ProjectCommissionStatus.SENT_TO_NEWSPAPER, ProjectCommissionStatus.SENT_TO_ADMINISTRATION, ProjectCommissionStatus.PUBLISHED_INTERNAL_NETWORK));
            }

            if (user.getRole() == RoleName.INTERNAL_NETWORK) {
                predicates.add(criteriaBuilder.isNotNull(project.get("domain")));
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

            if (StringUtils.isNotBlank(findContentFilterDTO.getEditorId())) {
                predicates.add(criteriaBuilder.equal(editor.get("id"), Integer.valueOf(findContentFilterDTO.getEditorId())));
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

    @Transactional
    public void assignToEditor(Integer id, Integer editorId) {
        Content content = this.findById(id);
        User user = this.userService.findById(editorId);
        content.setEditor(user);
        projectService.setStatusCommission(content.getProjectCommission().getProject().getId(), content.getProjectCommission().getId(), ProjectCommissionStatus.ASSIGNED.name());
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

    public List<WordpressCategoryResponse> getCategoriesWordpress(Integer id) {
        Content content = this.findById(id);
        Domain domain = content.getProjectCommission().getProject().getDomain();
        String wordpressUsername = domain.getWordpressUsername();
        String wordpressPassword = domain.getWordpressPassword();
        String encodedHeader = "Basic " + new String(Base64.getEncoder().encode((wordpressUsername + ":" + wordpressPassword).getBytes()));
        WordpressBaseApi baseApi = this.restTemplate.getForEntity("https://" + domain.getName() + "/index.php?rest_route=/", WordpressBaseApi.class).getBody();
        String baseUrl = baseApi.getUrl().replace("http://", "https://");

        return this.restTemplate
                .exchange(
                        RequestEntity.get(baseUrl + "/index.php?rest_route=/wp/v2/categories")
                                .header(HttpHeaders.AUTHORIZATION, encodedHeader)
                                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build(),
                        new ParameterizedTypeReference<List<WordpressCategoryResponse>>() {
                        }).getBody();
    }


    @Transactional
    public void publishOnWordpress(Integer id, PublishOnWordpressDTO publishOnWordpressDTO) {
        Content content = this.findById(id);
        Domain domain = content.getProjectCommission().getProject().getDomain();
        String wordpressUsername = domain.getWordpressUsername();
        String wordpressPassword = domain.getWordpressPassword();
        String encodedHeader = "Basic " + new String(Base64.getEncoder().encode((wordpressUsername + ":" + wordpressPassword).getBytes()));
        WordpressBaseApi baseApi = this.restTemplate.getForEntity("https://" + domain.getName() + "/index.php?rest_route=/", WordpressBaseApi.class).getBody();
        String baseUrl = baseApi.getUrl().replace("http://", "https://");


        log.info("Using header {}", encodedHeader);
        Map<String, Object> body = new HashMap<>();
        body.put("title", content.getProjectCommission().getTitle());
        body.put("status", "future");
        body.put("date", LocalDateTime.of(publishOnWordpressDTO.getPublishDate(), LocalTime.of(0, 0, 0)).format(DateTimeFormatter.ISO_DATE_TIME));
        body.put("content", content.getBody());
        body.put("categories", publishOnWordpressDTO.getCategories());

        String featuredMediaUrl = null;
        if (StringUtils.isNotBlank(publishOnWordpressDTO.getFeaturedMediaBase64())) {
            WordpressUploadMediaResponse wordpressUploadMediaResponse = this.publishMediaOnWordpress(baseUrl, encodedHeader, publishOnWordpressDTO.getFeaturedMediaBase64());
            body.put("featured_media", wordpressUploadMediaResponse.getId());
            featuredMediaUrl = wordpressUploadMediaResponse.getMediaDetails().getSizes().getFull().getSourceUrl();
        }
        if (publishOnWordpressDTO.getRemoveFeaturedMedia()) {
            body.put("featured_media", null);
        }

        try {
            RequestEntity<Map<String, Object>> request;
            if (content.getWordpressId() != null) {
                request = RequestEntity.post(baseUrl + "/index.php?rest_route=/wp/v2/posts/{id}", content.getWordpressId())
                        .header(HttpHeaders.AUTHORIZATION, encodedHeader)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body);
            } else {
                request = RequestEntity.post(baseUrl + "/index.php?rest_route=/wp/v2/posts")
                        .header(HttpHeaders.AUTHORIZATION, encodedHeader)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body);
            }

            WordpressCreatePostResponse response = this.restTemplate.exchange(request, WordpressCreatePostResponse.class).getBody();

            content.setWordpressId(String.valueOf(response.getId()));
            content.setWordpressPublicationDate(LocalDateTime.parse(response.getDate()));
            content.setWordpressUrl(response.getLink());
            content.setContentStatus(ContentStatus.PUBLISHED_WORDPRESS);

            content.setWordpressFeaturedMediaId(response.getFeaturedMedia());
            content.setWordpressFeaturedMediaUrl(featuredMediaUrl);

            content.getWordpressCategories().clear();
            content.getWordpressCategories().addAll(response.getCategories().stream().map(integer -> ContentWordpressCategory.builder().categoryId(integer).content(content).build()).collect(Collectors.toList()));
            this.contentRepository.save(content);
            this.projectService.setStatusCommission(content.getProjectCommission().getProject().getId(), content.getProjectCommission().getId(), String.valueOf(ProjectCommissionStatus.PUBLISHED_INTERNAL_NETWORK));
        } catch (Exception e) {
            log.error("Error publishing on wordpress", e);
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Pubblicazione fallita con il seguente messaggio: " + e.getMessage());
        }
    }

    public WordpressUploadMediaResponse publishMediaOnWordpress(String baseUrl, String encodedHeader, String fileUrl) {
        Pattern compile = Pattern.compile("(data:image\\/)(jpeg)(;base64,)(.*)");
        Matcher matcher = compile.matcher(fileUrl);
        if(matcher.find()){
            String extension = matcher.group(2);
            String base64 = matcher.group(4);

            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.put("file", List.of(new ByteArrayResource(Base64.getDecoder().decode(base64)){
                @Override
                public String getFilename() {
                    return "media." + extension; // Filename has to be returned in order to be able to post.
                }
            }));

            RequestEntity<LinkedMultiValueMap<String, Object>> requestEntity = RequestEntity.post(baseUrl + "/index.php?rest_route=/wp/v2/media")
                    .header(HttpHeaders.AUTHORIZATION, encodedHeader)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body);

            return this.restTemplate.exchange(requestEntity, WordpressUploadMediaResponse.class).getBody();
        }

        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
    }
}
