package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.model.PlatformSettings;
import it.xtreamdev.gflbe.service.PlatformSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
public class PlatformSettingsController {

    private final PlatformSettingsService platformSettingsService;

    @Autowired
    public PlatformSettingsController(PlatformSettingsService platformSettingsService) {
        this.platformSettingsService = platformSettingsService;
    }

    @GetMapping
    public List<PlatformSettings> getAllSettings() {
        return platformSettingsService.getSettings();
    }

    @PutMapping
    public void saveSettings(@RequestBody PlatformSettings save) {
        platformSettingsService.saveSettings(save);
    }
}
