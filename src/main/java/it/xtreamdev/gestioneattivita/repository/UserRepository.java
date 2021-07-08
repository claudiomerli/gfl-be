package it.xtreamdev.gestioneattivita.repository;

import it.xtreamdev.gestioneattivita.model.User;
import it.xtreamdev.gestioneattivita.model.enumerations.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Long countByRole(RoleName name);

    Page<User> findByRole(RoleName name, Pageable pageable);

    List<User> findByRole(RoleName editor);
}
