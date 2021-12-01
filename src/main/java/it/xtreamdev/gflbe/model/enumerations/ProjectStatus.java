package it.xtreamdev.gflbe.model.enumerations;

public enum ProjectStatus {
    CREATED,
    ASSIGNED,
    WORKING,
    TO_BE_PUBLISHED, // (quanto tutti gli articoli stanno in TO_BE_PUBLISHED)
    TERMINATED, // (quando tutti gli articoli stanno in PUBLISHED)
    INVOICED,
}
