
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
    "title",
    "robots",
    "canonical",
    "og_locale",
    "og_type",
    "og_title",
    "og_url",
    "og_site_name",
    "twitter_card",
    "schema"
})
@Generated("jsonschema2pojo")
public class YoastHeadJson {

    @JsonProperty("title")
    private String title;
    @JsonProperty("robots")
    private Robots robots;
    @JsonProperty("canonical")
    private String canonical;
    @JsonProperty("og_locale")
    private String ogLocale;
    @JsonProperty("og_type")
    private String ogType;
    @JsonProperty("og_title")
    private String ogTitle;
    @JsonProperty("og_url")
    private String ogUrl;
    @JsonProperty("og_site_name")
    private String ogSiteName;
    @JsonProperty("twitter_card")
    private String twitterCard;
    @JsonProperty("schema")
    private Schema schema;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("robots")
    public Robots getRobots() {
        return robots;
    }

    @JsonProperty("robots")
    public void setRobots(Robots robots) {
        this.robots = robots;
    }

    @JsonProperty("canonical")
    public String getCanonical() {
        return canonical;
    }

    @JsonProperty("canonical")
    public void setCanonical(String canonical) {
        this.canonical = canonical;
    }

    @JsonProperty("og_locale")
    public String getOgLocale() {
        return ogLocale;
    }

    @JsonProperty("og_locale")
    public void setOgLocale(String ogLocale) {
        this.ogLocale = ogLocale;
    }

    @JsonProperty("og_type")
    public String getOgType() {
        return ogType;
    }

    @JsonProperty("og_type")
    public void setOgType(String ogType) {
        this.ogType = ogType;
    }

    @JsonProperty("og_title")
    public String getOgTitle() {
        return ogTitle;
    }

    @JsonProperty("og_title")
    public void setOgTitle(String ogTitle) {
        this.ogTitle = ogTitle;
    }

    @JsonProperty("og_url")
    public String getOgUrl() {
        return ogUrl;
    }

    @JsonProperty("og_url")
    public void setOgUrl(String ogUrl) {
        this.ogUrl = ogUrl;
    }

    @JsonProperty("og_site_name")
    public String getOgSiteName() {
        return ogSiteName;
    }

    @JsonProperty("og_site_name")
    public void setOgSiteName(String ogSiteName) {
        this.ogSiteName = ogSiteName;
    }

    @JsonProperty("twitter_card")
    public String getTwitterCard() {
        return twitterCard;
    }

    @JsonProperty("twitter_card")
    public void setTwitterCard(String twitterCard) {
        this.twitterCard = twitterCard;
    }

    @JsonProperty("schema")
    public Schema getSchema() {
        return schema;
    }

    @JsonProperty("schema")
    public void setSchema(Schema schema) {
        this.schema = schema;
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
