package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.videotemplate.SaveVideoTemplate;
import it.xtreamdev.gflbe.model.VideoTemplate;
import it.xtreamdev.gflbe.service.VideoTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video-templates")
@RequiredArgsConstructor
public class VideoTemplateController {

    private final VideoTemplateService videoTemplateService;

    @GetMapping
    public List<VideoTemplate> getAllVideoTemplates() {
        return videoTemplateService.findAll();
    }

    @GetMapping("/{id}")
    public VideoTemplate getVideoTemplateById(@PathVariable Integer id) {
        return videoTemplateService.findById(id);
    }

    @PostMapping
    public VideoTemplate createVideoTemplate(@RequestBody SaveVideoTemplate saveVideoTemplate) {
        return videoTemplateService.save(saveVideoTemplate);
    }

    @PutMapping("/{id}")
    public VideoTemplate updateVideoTemplate(@PathVariable Integer id, @RequestBody SaveVideoTemplate saveVideoTemplate) {
        return videoTemplateService.update(id, saveVideoTemplate);
    }

    @DeleteMapping("/{id}")
    public void deleteVideoTemplate(@PathVariable Integer id) {
        videoTemplateService.delete(id);
    }
}
