package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Content;
import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.ContentProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Integer>, JpaSpecificationExecutor<Content> {

    Page<Content> findByEditorOrderByDeliveryDateDesc(User user, Pageable pageable);

    Optional<Content> findByIdAndEditor(Integer contentId, User user);

    boolean existsByProjectAndProjectStatusIn(Project project, List<ContentProjectStatus> asList);

    Integer countByNewspaper_Id(int id);
}
