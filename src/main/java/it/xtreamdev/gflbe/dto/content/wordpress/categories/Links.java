
package it.xtreamdev.gflbe.dto.content.wordpress.categories;

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
    "up",
    "wp:post_type",
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
    @JsonProperty("up")
    private List<Up> up;
    @JsonProperty("wp:post_type")
    private List<WpPostType> wpPostType;
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

    @JsonProperty("up")
    public List<Up> getUp() {
        return up;
    }

    @JsonProperty("up")
    public void setUp(List<Up> up) {
        this.up = up;
    }

    @JsonProperty("wp:post_type")
    public List<WpPostType> getWpPostType() {
        return wpPostType;
    }

    @JsonProperty("wp:post_type")
    public void setWpPostType(List<WpPostType> wpPostType) {
        this.wpPostType = wpPostType;
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
