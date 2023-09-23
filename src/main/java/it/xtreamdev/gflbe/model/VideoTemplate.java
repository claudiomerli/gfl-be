package it.xtreamdev.gflbe.model;

import it.xtreamdev.gflbe.model.enumerations.VideoTemplateType;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "video_template")
@Where(clause = "deleted = false")
public class VideoTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private VideoTemplateType type;

    private String name;

    private String url;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VideoTemplateField> fields = new ArrayList<>();

    @Builder.Default
    private Boolean deleted = false;

}
