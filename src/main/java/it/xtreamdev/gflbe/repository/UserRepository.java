package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndRoleInAndActiveTrue(String username, List<RoleName> roleName);

    List<User> findByEditorInfoIsNull();

    Optional<User> findByActivationCode(String code);
    Optional<User> findByEmailVerificationCode(String code);
    Optional<User> findByResetPasswordCode(String code);

    Optional<User> findByEmail(String email);

}
