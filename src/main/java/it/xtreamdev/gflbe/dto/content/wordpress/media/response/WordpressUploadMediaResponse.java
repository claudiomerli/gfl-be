
package it.xtreamdev.gflbe.dto.content.wordpress.media.response;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "date",
    "date_gmt",
    "guid",
    "modified",
    "modified_gmt",
    "slug",
    "status",
    "type",
    "link",
    "title",
    "author",
    "comment_status",
    "ping_status",
    "template",
    "meta",
    "permalink_template",
    "generated_slug",
    "yoast_head",
    "yoast_head_json",
    "description",
    "caption",
    "alt_text",
    "media_type",
    "mime_type",
    "media_details",
    "post",
    "source_url",
    "missing_image_sizes",
    "_links"
})
@Generated("jsonschema2pojo")
public class WordpressUploadMediaResponse {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("date")
    private String date;
    @JsonProperty("date_gmt")
    private String dateGmt;
    @JsonProperty("guid")
    private Guid guid;
    @JsonProperty("modified")
    private String modified;
    @JsonProperty("modified_gmt")
    private String modifiedGmt;
    @JsonProperty("slug")
    private String slug;
    @JsonProperty("status")
    private String status;
    @JsonProperty("type")
    private String type;
    @JsonProperty("link")
    private String link;
    @JsonProperty("title")
    private Title title;
    @JsonProperty("author")
    private Integer author;
    @JsonProperty("comment_status")
    private String commentStatus;
    @JsonProperty("ping_status")
    private String pingStatus;
    @JsonProperty("template")
    private String template;
    @JsonProperty("meta")
    private List<Object> meta;
    @JsonProperty("permalink_template")
    private String permalinkTemplate;
    @JsonProperty("generated_slug")
    private String generatedSlug;
    @JsonProperty("description")
    private Description description;
    @JsonProperty("caption")
    private Caption caption;
    @JsonProperty("alt_text")
    private String altText;
    @JsonProperty("media_type")
    private String mediaType;
    @JsonProperty("mime_type")
    private String mimeType;
    @JsonProperty("media_details")
    private MediaDetails mediaDetails;
    @JsonProperty("post")
    private Object post;
    @JsonProperty("source_url")
    private String sourceUrl;
    @JsonProperty("missing_image_sizes")
    private List<Object> missingImageSizes;
    @JsonProperty("_links")
    private Links links;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("date_gmt")
    public String getDateGmt() {
        return dateGmt;
    }

    @JsonProperty("date_gmt")
    public void setDateGmt(String dateGmt) {
        this.dateGmt = dateGmt;
    }

    @JsonProperty("guid")
    public Guid getGuid() {
        return guid;
    }

    @JsonProperty("guid")
    public void setGuid(Guid guid) {
        this.guid = guid;
    }

    @JsonProperty("modified")
    public String getModified() {
        return modified;
    }

    @JsonProperty("modified")
    public void setModified(String modified) {
        this.modified = modified;
    }

    @JsonProperty("modified_gmt")
    public String getModifiedGmt() {
        return modifiedGmt;
    }

    @JsonProperty("modified_gmt")
    public void setModifiedGmt(String modifiedGmt) {
        this.modifiedGmt = modifiedGmt;
    }

    @JsonProperty("slug")
    public String getSlug() {
        return slug;
    }

    @JsonProperty("slug")
    public void setSlug(String slug) {
        this.slug = slug;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(String link) {
        this.link = link;
    }

    @JsonProperty("title")
    public Title getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(Title title) {
        this.title = title;
    }

    @JsonProperty("author")
    public Integer getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(Integer author) {
        this.author = author;
    }

    @JsonProperty("comment_status")
    public String getCommentStatus() {
        return commentStatus;
    }

    @JsonProperty("comment_status")
    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    @JsonProperty("ping_status")
    public String getPingStatus() {
        return pingStatus;
    }

    @JsonProperty("ping_status")
    public void setPingStatus(String pingStatus) {
        this.pingStatus = pingStatus;
    }

    @JsonProperty("template")
    public String getTemplate() {
        return template;
    }

    @JsonProperty("template")
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty("meta")
    public List<Object> getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    public void setMeta(List<Object> meta) {
        this.meta = meta;
    }

    @JsonProperty("permalink_template")
    public String getPermalinkTemplate() {
        return permalinkTemplate;
    }

    @JsonProperty("permalink_template")
    public void setPermalinkTemplate(String permalinkTemplate) {
        this.permalinkTemplate = permalinkTemplate;
    }

    @JsonProperty("generated_slug")
    public String getGeneratedSlug() {
        return generatedSlug;
    }

    @JsonProperty("generated_slug")
    public void setGeneratedSlug(String generatedSlug) {
        this.generatedSlug = generatedSlug;
    }

    @JsonProperty("description")
    public Description getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(Description description) {
        this.description = description;
    }

    @JsonProperty("caption")
    public Caption getCaption() {
        return caption;
    }

    @JsonProperty("caption")
    public void setCaption(Caption caption) {
        this.caption = caption;
    }

    @JsonProperty("alt_text")
    public String getAltText() {
        return altText;
    }

    @JsonProperty("alt_text")
    public void setAltText(String altText) {
        this.altText = altText;
    }

    @JsonProperty("media_type")
    public String getMediaType() {
        return mediaType;
    }

    @JsonProperty("media_type")
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @JsonProperty("mime_type")
    public String getMimeType() {
        return mimeType;
    }

    @JsonProperty("mime_type")
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @JsonProperty("media_details")
    public MediaDetails getMediaDetails() {
        return mediaDetails;
    }

    @JsonProperty("media_details")
    public void setMediaDetails(MediaDetails mediaDetails) {
        this.mediaDetails = mediaDetails;
    }

    @JsonProperty("post")
    public Object getPost() {
        return post;
    }

    @JsonProperty("post")
    public void setPost(Object post) {
        this.post = post;
    }

    @JsonProperty("source_url")
    public String getSourceUrl() {
        return sourceUrl;
    }

    @JsonProperty("source_url")
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    @JsonProperty("missing_image_sizes")
    public List<Object> getMissingImageSizes() {
        return missingImageSizes;
    }

    @JsonProperty("missing_image_sizes")
    public void setMissingImageSizes(List<Object> missingImageSizes) {
        this.missingImageSizes = missingImageSizes;
    }

    @JsonProperty("_links")
    public Links getLinks() {
        return links;
    }

    @JsonProperty("_links")
    public void setLinks(Links links) {
        this.links = links;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
