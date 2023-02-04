package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.EditorInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EditorInfoRepository extends JpaRepository<EditorInfo, Integer>, JpaSpecificationExecutor<EditorInfo> {
}
