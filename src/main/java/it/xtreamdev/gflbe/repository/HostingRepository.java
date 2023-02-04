package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Hosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HostingRepository extends JpaRepository<Hosting, Integer>, JpaSpecificationExecutor<Hosting> {
}
