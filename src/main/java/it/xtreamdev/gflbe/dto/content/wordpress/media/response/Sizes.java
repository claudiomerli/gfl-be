
package it.xtreamdev.gflbe.dto.content.wordpress.media.response;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "medium",
    "large",
    "thumbnail",
    "medium_large",
    "featured-blog-large",
    "featured-blog-medium",
    "featured",
    "featured-blog-medium-small",
    "full"
})
@Generated("jsonschema2pojo")
public class Sizes {

    @JsonProperty("medium")
    private Medium medium;
    @JsonProperty("large")
    private Large large;
    @JsonProperty("thumbnail")
    private Thumbnail thumbnail;
    @JsonProperty("medium_large")
    private MediumLarge mediumLarge;
    @JsonProperty("featured-blog-large")
    private FeaturedBlogLarge featuredBlogLarge;
    @JsonProperty("featured-blog-medium")
    private FeaturedBlogMedium featuredBlogMedium;
    @JsonProperty("featured")
    private Featured featured;
    @JsonProperty("featured-blog-medium-small")
    private FeaturedBlogMediumSmall featuredBlogMediumSmall;
    @JsonProperty("full")
    private Full full;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("medium")
    public Medium getMedium() {
        return medium;
    }

    @JsonProperty("medium")
    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    @JsonProperty("large")
    public Large getLarge() {
        return large;
    }

    @JsonProperty("large")
    public void setLarge(Large large) {
        this.large = large;
    }

    @JsonProperty("thumbnail")
    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    @JsonProperty("thumbnail")
    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    @JsonProperty("medium_large")
    public MediumLarge getMediumLarge() {
        return mediumLarge;
    }

    @JsonProperty("medium_large")
    public void setMediumLarge(MediumLarge mediumLarge) {
        this.mediumLarge = mediumLarge;
    }

    @JsonProperty("featured-blog-large")
    public FeaturedBlogLarge getFeaturedBlogLarge() {
        return featuredBlogLarge;
    }

    @JsonProperty("featured-blog-large")
    public void setFeaturedBlogLarge(FeaturedBlogLarge featuredBlogLarge) {
        this.featuredBlogLarge = featuredBlogLarge;
    }

    @JsonProperty("featured-blog-medium")
    public FeaturedBlogMedium getFeaturedBlogMedium() {
        return featuredBlogMedium;
    }

    @JsonProperty("featured-blog-medium")
    public void setFeaturedBlogMedium(FeaturedBlogMedium featuredBlogMedium) {
        this.featuredBlogMedium = featuredBlogMedium;
    }

    @JsonProperty("featured")
    public Featured getFeatured() {
        return featured;
    }

    @JsonProperty("featured")
    public void setFeatured(Featured featured) {
        this.featured = featured;
    }

    @JsonProperty("featured-blog-medium-small")
    public FeaturedBlogMediumSmall getFeaturedBlogMediumSmall() {
        return featuredBlogMediumSmall;
    }

    @JsonProperty("featured-blog-medium-small")
    public void setFeaturedBlogMediumSmall(FeaturedBlogMediumSmall featuredBlogMediumSmall) {
        this.featuredBlogMediumSmall = featuredBlogMediumSmall;
    }

    @JsonProperty("full")
    public Full getFull() {
        return full;
    }

    @JsonProperty("full")
    public void setFull(Full full) {
        this.full = full;
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
