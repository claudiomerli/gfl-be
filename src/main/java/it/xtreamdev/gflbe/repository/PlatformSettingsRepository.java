package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.PlatformSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformSettingsRepository extends JpaRepository<PlatformSettings, String> {
}
