package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Attachment;
import it.xtreamdev.gflbe.model.ContentHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.transaction.Transactional;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer>, JpaSpecificationExecutor<Attachment> {

    @Transactional
    void deleteByIdAndContentHint(Integer id, ContentHint contentHint);

}
