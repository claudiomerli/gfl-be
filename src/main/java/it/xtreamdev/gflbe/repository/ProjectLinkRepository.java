package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.model.ProjectLink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectLinkRepository extends JpaRepository<ProjectLink, Integer> {
    List<ProjectLink> findByProject(Project project, Sort pageable);
}
