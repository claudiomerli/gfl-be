package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>, JpaSpecificationExecutor<Project> {
    List<Project> findAllByCustomer_Id(Integer idCustomer);
}
