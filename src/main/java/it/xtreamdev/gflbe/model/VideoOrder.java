package it.xtreamdev.gflbe.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("VIDEO")
public class VideoOrder extends GenericOrder {

    @ManyToOne
    private VideoTemplate videoTemplate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VideoOrderField> fields = new ArrayList<>();

}
