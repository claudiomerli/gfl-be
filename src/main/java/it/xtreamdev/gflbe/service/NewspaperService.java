package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.repository.NewspaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewspaperService {

    @Autowired
    private NewspaperRepository newspaperRepository;

    public List<Newspaper> findAll() {
        return this.newspaperRepository.findAll();
    }

    public void save(Newspaper newspaper) {
        this.newspaperRepository.save(newspaper);
    }

    public void delete(Integer id) {
        this.newspaperRepository.deleteById(id);
    }

}
