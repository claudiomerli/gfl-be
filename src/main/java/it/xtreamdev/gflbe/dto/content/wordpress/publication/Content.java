
package it.xtreamdev.gflbe.dto.content.wordpress.publication;

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
    "raw",
    "rendered",
    "protected",
    "block_version"
})
@Generated("jsonschema2pojo")
public class Content {

    @JsonProperty("raw")
    private String raw;
    @JsonProperty("rendered")
    private String rendered;
    @JsonProperty("protected")
    private Boolean _protected;
    @JsonProperty("block_version")
    private Integer blockVersion;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("raw")
    public String getRaw() {
        return raw;
    }

    @JsonProperty("raw")
    public void setRaw(String raw) {
        this.raw = raw;
    }

    @JsonProperty("rendered")
    public String getRendered() {
        return rendered;
    }

    @JsonProperty("rendered")
    public void setRendered(String rendered) {
        this.rendered = rendered;
    }

    @JsonProperty("protected")
    public Boolean getProtected() {
        return _protected;
    }

    @JsonProperty("protected")
    public void setProtected(Boolean _protected) {
        this._protected = _protected;
    }

    @JsonProperty("block_version")
    public Integer getBlockVersion() {
        return blockVersion;
    }

    @JsonProperty("block_version")
    public void setBlockVersion(Integer blockVersion) {
        this.blockVersion = blockVersion;
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
