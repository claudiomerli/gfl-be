
package it.xtreamdev.gflbe.dto.content.wordpress.media.response;

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
    "aperture",
    "credit",
    "camera",
    "caption",
    "created_timestamp",
    "copyright",
    "focal_length",
    "iso",
    "shutter_speed",
    "title",
    "orientation",
    "keywords"
})
@Generated("jsonschema2pojo")
public class ImageMeta {

    @JsonProperty("aperture")
    private String aperture;
    @JsonProperty("credit")
    private String credit;
    @JsonProperty("camera")
    private String camera;
    @JsonProperty("caption")
    private String caption;
    @JsonProperty("created_timestamp")
    private String createdTimestamp;
    @JsonProperty("copyright")
    private String copyright;
    @JsonProperty("focal_length")
    private String focalLength;
    @JsonProperty("iso")
    private String iso;
    @JsonProperty("shutter_speed")
    private String shutterSpeed;
    @JsonProperty("title")
    private String title;
    @JsonProperty("orientation")
    private String orientation;
    @JsonProperty("keywords")
    private List<Object> keywords;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("aperture")
    public String getAperture() {
        return aperture;
    }

    @JsonProperty("aperture")
    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    @JsonProperty("credit")
    public String getCredit() {
        return credit;
    }

    @JsonProperty("credit")
    public void setCredit(String credit) {
        this.credit = credit;
    }

    @JsonProperty("camera")
    public String getCamera() {
        return camera;
    }

    @JsonProperty("camera")
    public void setCamera(String camera) {
        this.camera = camera;
    }

    @JsonProperty("caption")
    public String getCaption() {
        return caption;
    }

    @JsonProperty("caption")
    public void setCaption(String caption) {
        this.caption = caption;
    }

    @JsonProperty("created_timestamp")
    public String getCreatedTimestamp() {
        return createdTimestamp;
    }

    @JsonProperty("created_timestamp")
    public void setCreatedTimestamp(String createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @JsonProperty("copyright")
    public String getCopyright() {
        return copyright;
    }

    @JsonProperty("copyright")
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    @JsonProperty("focal_length")
    public String getFocalLength() {
        return focalLength;
    }

    @JsonProperty("focal_length")
    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    @JsonProperty("iso")
    public String getIso() {
        return iso;
    }

    @JsonProperty("iso")
    public void setIso(String iso) {
        this.iso = iso;
    }

    @JsonProperty("shutter_speed")
    public String getShutterSpeed() {
        return shutterSpeed;
    }

    @JsonProperty("shutter_speed")
    public void setShutterSpeed(String shutterSpeed) {
        this.shutterSpeed = shutterSpeed;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("orientation")
    public String getOrientation() {
        return orientation;
    }

    @JsonProperty("orientation")
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    @JsonProperty("keywords")
    public List<Object> getKeywords() {
        return keywords;
    }

    @JsonProperty("keywords")
    public void setKeywords(List<Object> keywords) {
        this.keywords = keywords;
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
