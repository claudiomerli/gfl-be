package it.xtreamdev.gflbe.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("SECOND_LEVEL")
public class SecondLevelOrder extends GenericOrder {

    private String link;

}
