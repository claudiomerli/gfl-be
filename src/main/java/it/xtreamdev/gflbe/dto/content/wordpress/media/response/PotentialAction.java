
package it.xtreamdev.gflbe.dto.content.wordpress.media.response;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "@type",
    "target",
    "query-input"
})
@Generated("jsonschema2pojo")
public class PotentialAction {

    @JsonProperty("@type")
    private String type;
    @JsonProperty("target")
    private Target target;
    @JsonProperty("query-input")
    private String queryInput;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("@type")
    public String getType() {
        return type;
    }

    @JsonProperty("@type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("target")
    public Target getTarget() {
        return target;
    }

    @JsonProperty("target")
    public void setTarget(Target target) {
        this.target = target;
    }

    @JsonProperty("query-input")
    public String getQueryInput() {
        return queryInput;
    }

    @JsonProperty("query-input")
    public void setQueryInput(String queryInput) {
        this.queryInput = queryInput;
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
