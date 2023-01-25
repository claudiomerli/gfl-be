package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.model.ProjectCommission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectCommissionRepository extends JpaRepository<ProjectCommission, Integer>, JpaSpecificationExecutor<ProjectCommission> {

    Page<ProjectCommission> findByContentIsNull(Pageable page);

    List<ProjectCommission> findByProject(Project project, Sort sort);

}
