package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Long countByRole(RoleName name);

    Page<User> findByRole(RoleName name, Pageable pageable);

    List<User> findByRole(RoleName editor);

    @Transactional
    void deleteByIdAndRole(Integer id, RoleName roleName);
}
