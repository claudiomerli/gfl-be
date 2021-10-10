package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Content;
import it.xtreamdev.gflbe.model.ContentLink;
import it.xtreamdev.gflbe.model.ContentRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentLinkRepository extends JpaRepository<ContentLink, Integer> {

    @Modifying
    @Query("delete from ContentLink cl where cl.content=:content")
    void deleteByContent(@Param("content") Content content);

    void deleteByContentRules(ContentRules contentRules);

}
