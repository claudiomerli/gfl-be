
package it.xtreamdev.gflbe.dto.content.wordpress.publication;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "self",
    "collection",
    "about",
    "author",
    "replies",
    "version-history",
    "wp:attachment",
    "wp:term",
    "wp:action-publish",
    "wp:action-unfiltered-html",
    "wp:action-sticky",
    "wp:action-assign-author",
    "wp:action-create-categories",
    "wp:action-assign-categories",
    "wp:action-create-tags",
    "wp:action-assign-tags",
    "curies"
})
@Generated("jsonschema2pojo")
public class Links {

    @JsonProperty("self")
    private List<Self> self;
    @JsonProperty("collection")
    private List<Collection> collection;
    @JsonProperty("about")
    private List<About> about;
    @JsonProperty("author")
    private List<Author> author;
    @JsonProperty("replies")
    private List<Reply> replies;
    @JsonProperty("version-history")
    private List<VersionHistory> versionHistory;
    @JsonProperty("wp:attachment")
    private List<WpAttachment> wpAttachment;
    @JsonProperty("wp:term")
    private List<WpTerm> wpTerm;
    @JsonProperty("wp:action-publish")
    private List<WpActionPublish> wpActionPublish;
    @JsonProperty("wp:action-unfiltered-html")
    private List<WpActionUnfilteredHtml> wpActionUnfilteredHtml;
    @JsonProperty("wp:action-sticky")
    private List<WpActionSticky> wpActionSticky;
    @JsonProperty("wp:action-assign-author")
    private List<WpActionAssignAuthor> wpActionAssignAuthor;
    @JsonProperty("wp:action-create-categories")
    private List<WpActionCreateCategory> wpActionCreateCategories;
    @JsonProperty("wp:action-assign-categories")
    private List<WpActionAssignCategory> wpActionAssignCategories;
    @JsonProperty("wp:action-create-tags")
    private List<WpActionCreateTag> wpActionCreateTags;
    @JsonProperty("wp:action-assign-tags")
    private List<WpActionAssignTag> wpActionAssignTags;
    @JsonProperty("curies")
    private List<Cury> curies;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("self")
    public List<Self> getSelf() {
        return self;
    }

    @JsonProperty("self")
    public void setSelf(List<Self> self) {
        this.self = self;
    }

    @JsonProperty("collection")
    public List<Collection> getCollection() {
        return collection;
    }

    @JsonProperty("collection")
    public void setCollection(List<Collection> collection) {
        this.collection = collection;
    }

    @JsonProperty("about")
    public List<About> getAbout() {
        return about;
    }

    @JsonProperty("about")
    public void setAbout(List<About> about) {
        this.about = about;
    }

    @JsonProperty("author")
    public List<Author> getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(List<Author> author) {
        this.author = author;
    }

    @JsonProperty("replies")
    public List<Reply> getReplies() {
        return replies;
    }

    @JsonProperty("replies")
    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    @JsonProperty("version-history")
    public List<VersionHistory> getVersionHistory() {
        return versionHistory;
    }

    @JsonProperty("version-history")
    public void setVersionHistory(List<VersionHistory> versionHistory) {
        this.versionHistory = versionHistory;
    }

    @JsonProperty("wp:attachment")
    public List<WpAttachment> getWpAttachment() {
        return wpAttachment;
    }

    @JsonProperty("wp:attachment")
    public void setWpAttachment(List<WpAttachment> wpAttachment) {
        this.wpAttachment = wpAttachment;
    }

    @JsonProperty("wp:term")
    public List<WpTerm> getWpTerm() {
        return wpTerm;
    }

    @JsonProperty("wp:term")
    public void setWpTerm(List<WpTerm> wpTerm) {
        this.wpTerm = wpTerm;
    }

    @JsonProperty("wp:action-publish")
    public List<WpActionPublish> getWpActionPublish() {
        return wpActionPublish;
    }

    @JsonProperty("wp:action-publish")
    public void setWpActionPublish(List<WpActionPublish> wpActionPublish) {
        this.wpActionPublish = wpActionPublish;
    }

    @JsonProperty("wp:action-unfiltered-html")
    public List<WpActionUnfilteredHtml> getWpActionUnfilteredHtml() {
        return wpActionUnfilteredHtml;
    }

    @JsonProperty("wp:action-unfiltered-html")
    public void setWpActionUnfilteredHtml(List<WpActionUnfilteredHtml> wpActionUnfilteredHtml) {
        this.wpActionUnfilteredHtml = wpActionUnfilteredHtml;
    }

    @JsonProperty("wp:action-sticky")
    public List<WpActionSticky> getWpActionSticky() {
        return wpActionSticky;
    }

    @JsonProperty("wp:action-sticky")
    public void setWpActionSticky(List<WpActionSticky> wpActionSticky) {
        this.wpActionSticky = wpActionSticky;
    }

    @JsonProperty("wp:action-assign-author")
    public List<WpActionAssignAuthor> getWpActionAssignAuthor() {
        return wpActionAssignAuthor;
    }

    @JsonProperty("wp:action-assign-author")
    public void setWpActionAssignAuthor(List<WpActionAssignAuthor> wpActionAssignAuthor) {
        this.wpActionAssignAuthor = wpActionAssignAuthor;
    }

    @JsonProperty("wp:action-create-categories")
    public List<WpActionCreateCategory> getWpActionCreateCategories() {
        return wpActionCreateCategories;
    }

    @JsonProperty("wp:action-create-categories")
    public void setWpActionCreateCategories(List<WpActionCreateCategory> wpActionCreateCategories) {
        this.wpActionCreateCategories = wpActionCreateCategories;
    }

    @JsonProperty("wp:action-assign-categories")
    public List<WpActionAssignCategory> getWpActionAssignCategories() {
        return wpActionAssignCategories;
    }

    @JsonProperty("wp:action-assign-categories")
    public void setWpActionAssignCategories(List<WpActionAssignCategory> wpActionAssignCategories) {
        this.wpActionAssignCategories = wpActionAssignCategories;
    }

    @JsonProperty("wp:action-create-tags")
    public List<WpActionCreateTag> getWpActionCreateTags() {
        return wpActionCreateTags;
    }

    @JsonProperty("wp:action-create-tags")
    public void setWpActionCreateTags(List<WpActionCreateTag> wpActionCreateTags) {
        this.wpActionCreateTags = wpActionCreateTags;
    }

    @JsonProperty("wp:action-assign-tags")
    public List<WpActionAssignTag> getWpActionAssignTags() {
        return wpActionAssignTags;
    }

    @JsonProperty("wp:action-assign-tags")
    public void setWpActionAssignTags(List<WpActionAssignTag> wpActionAssignTags) {
        this.wpActionAssignTags = wpActionAssignTags;
    }

    @JsonProperty("curies")
    public List<Cury> getCuries() {
        return curies;
    }

    @JsonProperty("curies")
    public void setCuries(List<Cury> curies) {
        this.curies = curies;
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
