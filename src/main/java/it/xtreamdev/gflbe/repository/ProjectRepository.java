package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Domain;
import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>, JpaSpecificationExecutor<Project> {
    Page<Project> findByHintIsNull(Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Project set domain = null where domain = :domain")
    void setDomainToNullWhereDomain(@Param("domain") Domain domain);

    List<Project> findByCustomer(User user);

}
