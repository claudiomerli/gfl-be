package it.xtreamdev.gflbe.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "attachment_data")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttachmentData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Integer id;

    @Lob
    @Column(name = "bytes")
    private byte[] bytes;

}
