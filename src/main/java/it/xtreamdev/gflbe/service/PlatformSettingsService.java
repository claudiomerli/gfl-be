package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.model.PlatformSettings;
import it.xtreamdev.gflbe.repository.PlatformSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformSettingsService {

    @Autowired
    private PlatformSettingsRepository platformSettingsRepository;

    public List<PlatformSettings> getSettings(){
        return this.platformSettingsRepository.findAll();
    }

    public void saveSettings(PlatformSettings save){
        this.platformSettingsRepository.save(save);
    }

}
