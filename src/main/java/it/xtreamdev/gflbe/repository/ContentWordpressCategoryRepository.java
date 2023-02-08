package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Content;
import it.xtreamdev.gflbe.model.ContentWordpressCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentWordpressCategoryRepository extends JpaRepository<ContentWordpressCategory, Integer> {

    void deleteByContent(Content content);

}
