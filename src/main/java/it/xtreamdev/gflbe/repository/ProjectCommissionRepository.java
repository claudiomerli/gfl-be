package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.ProjectCommission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectCommissionRepository extends JpaRepository<ProjectCommission, Integer> {

    List<ProjectCommission>  findByContentIsNull();

}
