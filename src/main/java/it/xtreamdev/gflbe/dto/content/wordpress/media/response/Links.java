
package it.xtreamdev.gflbe.dto.content.wordpress.media.response;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "self",
    "collection",
    "about",
    "author",
    "replies",
    "wp:action-unfiltered-html",
    "wp:action-assign-author",
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
    @JsonProperty("wp:action-unfiltered-html")
    private List<WpActionUnfilteredHtml> wpActionUnfilteredHtml;
    @JsonProperty("wp:action-assign-author")
    private List<WpActionAssignAuthor> wpActionAssignAuthor;
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

    @JsonProperty("wp:action-unfiltered-html")
    public List<WpActionUnfilteredHtml> getWpActionUnfilteredHtml() {
        return wpActionUnfilteredHtml;
    }

    @JsonProperty("wp:action-unfiltered-html")
    public void setWpActionUnfilteredHtml(List<WpActionUnfilteredHtml> wpActionUnfilteredHtml) {
        this.wpActionUnfilteredHtml = wpActionUnfilteredHtml;
    }

    @JsonProperty("wp:action-assign-author")
    public List<WpActionAssignAuthor> getWpActionAssignAuthor() {
        return wpActionAssignAuthor;
    }

    @JsonProperty("wp:action-assign-author")
    public void setWpActionAssignAuthor(List<WpActionAssignAuthor> wpActionAssignAuthor) {
        this.wpActionAssignAuthor = wpActionAssignAuthor;
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
