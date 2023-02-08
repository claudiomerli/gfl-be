
package it.xtreamdev.gflbe.dto.content.wordpress.categories;

import java.util.LinkedHashMap;
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
    "index",
    "follow",
    "max-snippet",
    "max-image-preview",
    "max-video-preview"
})
@Generated("jsonschema2pojo")
public class Robots {

    @JsonProperty("index")
    private String index;
    @JsonProperty("follow")
    private String follow;
    @JsonProperty("max-snippet")
    private String maxSnippet;
    @JsonProperty("max-image-preview")
    private String maxImagePreview;
    @JsonProperty("max-video-preview")
    private String maxVideoPreview;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("index")
    public String getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(String index) {
        this.index = index;
    }

    @JsonProperty("follow")
    public String getFollow() {
        return follow;
    }

    @JsonProperty("follow")
    public void setFollow(String follow) {
        this.follow = follow;
    }

    @JsonProperty("max-snippet")
    public String getMaxSnippet() {
        return maxSnippet;
    }

    @JsonProperty("max-snippet")
    public void setMaxSnippet(String maxSnippet) {
        this.maxSnippet = maxSnippet;
    }

    @JsonProperty("max-image-preview")
    public String getMaxImagePreview() {
        return maxImagePreview;
    }

    @JsonProperty("max-image-preview")
    public void setMaxImagePreview(String maxImagePreview) {
        this.maxImagePreview = maxImagePreview;
    }

    @JsonProperty("max-video-preview")
    public String getMaxVideoPreview() {
        return maxVideoPreview;
    }

    @JsonProperty("max-video-preview")
    public void setMaxVideoPreview(String maxVideoPreview) {
        this.maxVideoPreview = maxVideoPreview;
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
