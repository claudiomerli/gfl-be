package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.videotemplate.SaveVideoTemplate;
import it.xtreamdev.gflbe.model.VideoTemplate;
import it.xtreamdev.gflbe.model.VideoTemplateField;
import it.xtreamdev.gflbe.repository.VideoTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoTemplateService {

    @Autowired
    private VideoTemplateRepository videoTemplateRepository;

    public List<VideoTemplate> findAll() {
        return this.videoTemplateRepository.findAll();
    }

    public VideoTemplate findById(Integer id) {
        return this.videoTemplateRepository.findById(id).orElseThrow();
    }

    public VideoTemplate update(Integer id, SaveVideoTemplate saveVideoTemplate) {
        VideoTemplate videoTemplate = this.findById(id);
        videoTemplate.setType(saveVideoTemplate.getType());
        videoTemplate.setName(saveVideoTemplate.getName());
        videoTemplate.setUrl(saveVideoTemplate.getUrl());

        videoTemplate.getFields().clear();
        videoTemplate.getFields().addAll(saveVideoTemplate.getFields().stream().map(mapEntry ->
                VideoTemplateField.builder().field(mapEntry.getName()).template(videoTemplate).build()
        ).collect(Collectors.toList()));
        return this.videoTemplateRepository.save(videoTemplate);
    }

    public VideoTemplate save(SaveVideoTemplate saveVideoTemplate) {
        VideoTemplate videoTemplate = VideoTemplate
                .builder()
                .url(saveVideoTemplate.getUrl())
                .name(saveVideoTemplate.getName())
                .type(saveVideoTemplate.getType())
                .build();

        videoTemplate.getFields().addAll(saveVideoTemplate.getFields().stream().map(mapEntry ->
                VideoTemplateField.builder().field(mapEntry.getName()).template(videoTemplate).build()
        ).collect(Collectors.toList()));

        return this.videoTemplateRepository.save(videoTemplate);
    }

    public void delete(Integer id) {
        VideoTemplate videoTemplate = this.findById(id);
        videoTemplate.setDeleted(true);
        this.videoTemplateRepository.save(videoTemplate);
    }

}
