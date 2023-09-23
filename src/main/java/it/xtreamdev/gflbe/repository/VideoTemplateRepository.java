package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.VideoTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VideoTemplateRepository extends JpaRepository<VideoTemplate, Integer>, JpaSpecificationExecutor<VideoTemplate> {
}
