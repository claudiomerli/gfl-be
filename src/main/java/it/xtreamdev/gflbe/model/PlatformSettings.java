package it.xtreamdev.gflbe.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "platform_settings")
public class PlatformSettings {

    @Id
    private String code;

    @Lob
    private String value;

}
