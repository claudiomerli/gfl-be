package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.project.SaveContentHintTemplate;
import it.xtreamdev.gflbe.model.ContentHintTemplate;
import it.xtreamdev.gflbe.repository.ContentHintTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentHintTemplateService {

    @Autowired
    private ContentHintTemplateRepository contentHintTemplateRepository;

    public List<ContentHintTemplate> findAll(){
        return this.contentHintTemplateRepository.findAll();
    }

    public void delete(Integer id){
        this.contentHintTemplateRepository.deleteById(id);
    }

    public void save(SaveContentHintTemplate saveContentHintTemplate) {
        this.contentHintTemplateRepository.save(
                ContentHintTemplate.builder().body(saveContentHintTemplate.getBody()).build()
        );
    }
}
