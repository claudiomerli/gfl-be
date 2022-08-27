package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.FindNewsDTO;
import it.xtreamdev.gflbe.dto.SaveNewsDTO;
import it.xtreamdev.gflbe.model.News;
import it.xtreamdev.gflbe.repository.NewsRepository;
import org.apache.commons.collections4.list.PredicatedList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    public Page<News> findNews(FindNewsDTO findNewsDTO, PageRequest pageRequest) {
        return this.newsRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(findNewsDTO.getGlobalSearch())) {
                Arrays.asList(findNewsDTO.getGlobalSearch().split(" ")).forEach(portion -> {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("title")), "%" + portion.toUpperCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("body")), "%" + portion.toUpperCase() + "%")
                    ));
                });
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }), pageRequest);
    }

    public News findById(Integer id) {
        return this.newsRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
    }

    public News save(SaveNewsDTO saveNewsDTO) {
        return this.newsRepository.save(News
                .builder()
                .title(saveNewsDTO.getTitle())
                .body(saveNewsDTO.getBody())
                .image(saveNewsDTO.getImage())
                .build());
    }

    public News update(Integer id, SaveNewsDTO saveNewsDTO) {
        News newsToUpdate = this.findById(id);

        newsToUpdate.setTitle(saveNewsDTO.getTitle());
        newsToUpdate.setBody(saveNewsDTO.getBody());
        newsToUpdate.setImage(saveNewsDTO.getImage());

        return this.newsRepository.save(newsToUpdate);
    }

    public void delete(Integer id) {
        this.newsRepository.deleteById(id);
    }


}
