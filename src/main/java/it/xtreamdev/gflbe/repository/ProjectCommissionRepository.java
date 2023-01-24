package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.ProjectCommission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectCommissionRepository extends JpaRepository<ProjectCommission, Integer> {

    Slice<ProjectCommission> findByContentIsNull(Pageable page);

}
