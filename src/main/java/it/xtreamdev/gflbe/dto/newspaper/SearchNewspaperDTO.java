package it.xtreamdev.gflbe.dto.newspaper;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchNewspaperDTO {

    private String name;
    private String id;
    private Long zaFrom;
    private Long zaTo;
    private Long purchasedContentFrom;
    private Long purchasedContentTo;
    private Long leftContentFrom;
    private Long leftContentTo;
    private Double costEachFrom;
    private Double costEachTo;
    private Double costSellFrom;
    private Double costSellTo;
    private List<String> regionalGeolocalization = new ArrayList<>();
    private List<Integer> topics = new ArrayList<>();

}
