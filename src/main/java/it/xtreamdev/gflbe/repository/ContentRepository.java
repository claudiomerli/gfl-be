package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Integer>, JpaSpecificationExecutor<Content> {

    Page<Content> findByHintIsNull(Pageable pageable);
}
