
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
    "@type",
    "@id",
    "url",
    "name",
    "isPartOf",
    "datePublished",
    "dateModified",
    "breadcrumb",
    "inLanguage",
    "potentialAction",
    "itemListElement",
    "description",
    "publisher",
    "logo",
    "image"
})
@Generated("jsonschema2pojo")
public class Graph {

    @JsonProperty("@type")
    private String type;
    @JsonProperty("@id")
    private String id;
    @JsonProperty("url")
    private String url;
    @JsonProperty("name")
    private String name;
    @JsonProperty("isPartOf")
    private IsPartOf isPartOf;
    @JsonProperty("datePublished")
    private String datePublished;
    @JsonProperty("dateModified")
    private String dateModified;
    @JsonProperty("breadcrumb")
    private Breadcrumb breadcrumb;
    @JsonProperty("inLanguage")
    private String inLanguage;
    @JsonProperty("potentialAction")
    private List<PotentialAction> potentialAction;
    @JsonProperty("itemListElement")
    private List<ItemListElement> itemListElement;
    @JsonProperty("description")
    private String description;
    @JsonProperty("publisher")
    private Publisher publisher;
    @JsonProperty("logo")
    private Logo logo;
    @JsonProperty("image")
    private Image image;
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

    @JsonProperty("@id")
    public String getId() {
        return id;
    }

    @JsonProperty("@id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("isPartOf")
    public IsPartOf getIsPartOf() {
        return isPartOf;
    }

    @JsonProperty("isPartOf")
    public void setIsPartOf(IsPartOf isPartOf) {
        this.isPartOf = isPartOf;
    }

    @JsonProperty("datePublished")
    public String getDatePublished() {
        return datePublished;
    }

    @JsonProperty("datePublished")
    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    @JsonProperty("dateModified")
    public String getDateModified() {
        return dateModified;
    }

    @JsonProperty("dateModified")
    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    @JsonProperty("breadcrumb")
    public Breadcrumb getBreadcrumb() {
        return breadcrumb;
    }

    @JsonProperty("breadcrumb")
    public void setBreadcrumb(Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
    }

    @JsonProperty("inLanguage")
    public String getInLanguage() {
        return inLanguage;
    }

    @JsonProperty("inLanguage")
    public void setInLanguage(String inLanguage) {
        this.inLanguage = inLanguage;
    }

    @JsonProperty("potentialAction")
    public List<PotentialAction> getPotentialAction() {
        return potentialAction;
    }

    @JsonProperty("potentialAction")
    public void setPotentialAction(List<PotentialAction> potentialAction) {
        this.potentialAction = potentialAction;
    }

    @JsonProperty("itemListElement")
    public List<ItemListElement> getItemListElement() {
        return itemListElement;
    }

    @JsonProperty("itemListElement")
    public void setItemListElement(List<ItemListElement> itemListElement) {
        this.itemListElement = itemListElement;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("publisher")
    public Publisher getPublisher() {
        return publisher;
    }

    @JsonProperty("publisher")
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    @JsonProperty("logo")
    public Logo getLogo() {
        return logo;
    }

    @JsonProperty("logo")
    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    @JsonProperty("image")
    public Image getImage() {
        return image;
    }

    @JsonProperty("image")
    public void setImage(Image image) {
        this.image = image;
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
