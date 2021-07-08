package it.xtreamdev.gestioneattivita.repository;

import it.xtreamdev.gestioneattivita.model.Content;
import it.xtreamdev.gestioneattivita.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Integer>, JpaSpecificationExecutor<Content> {

    Page<Content> findByEditorOrderByDeliveryDateDesc(User user, Pageable pageable);

    Optional<Content> findByIdAndEditor(Integer contentId, User user);
}
