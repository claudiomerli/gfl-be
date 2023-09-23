package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "video_template_field")
public class VideoTemplateField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String field;

    @ManyToOne
    @JsonIgnore
    private VideoTemplate template;


}
