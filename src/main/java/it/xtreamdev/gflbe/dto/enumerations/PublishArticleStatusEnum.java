package it.xtreamdev.gflbe.dto.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PublishArticleStatusEnum {
    PUBLISH("publish"),
    FUTURE("future"),
    DRAFT("draft"),
    PENDING("pending"),
    PRIVAT("private");

    private final String status;
}
