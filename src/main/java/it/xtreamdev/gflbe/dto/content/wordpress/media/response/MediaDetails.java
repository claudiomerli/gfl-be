
package it.xtreamdev.gflbe.dto.content.wordpress.media.response;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "width",
    "height",
    "file",
    "filesize",
    "sizes",
    "image_meta"
})
@Generated("jsonschema2pojo")
public class MediaDetails {

    @JsonProperty("width")
    private Integer width;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("file")
    private String file;
    @JsonProperty("filesize")
    private Integer filesize;
    @JsonProperty("sizes")
    private Sizes sizes;
    @JsonProperty("image_meta")
    private ImageMeta imageMeta;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("width")
    public Integer getWidth() {
        return width;
    }

    @JsonProperty("width")
    public void setWidth(Integer width) {
        this.width = width;
    }

    @JsonProperty("height")
    public Integer getHeight() {
        return height;
    }

    @JsonProperty("height")
    public void setHeight(Integer height) {
        this.height = height;
    }

    @JsonProperty("file")
    public String getFile() {
        return file;
    }

    @JsonProperty("file")
    public void setFile(String file) {
        this.file = file;
    }

    @JsonProperty("filesize")
    public Integer getFilesize() {
        return filesize;
    }

    @JsonProperty("filesize")
    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }

    @JsonProperty("sizes")
    public Sizes getSizes() {
        return sizes;
    }

    @JsonProperty("sizes")
    public void setSizes(Sizes sizes) {
        this.sizes = sizes;
    }

    @JsonProperty("image_meta")
    public ImageMeta getImageMeta() {
        return imageMeta;
    }

    @JsonProperty("image_meta")
    public void setImageMeta(ImageMeta imageMeta) {
        this.imageMeta = imageMeta;
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
