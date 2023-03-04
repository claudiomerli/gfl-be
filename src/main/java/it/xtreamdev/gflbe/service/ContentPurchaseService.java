package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.purchasecontent.SaveContentPurchaseDTO;
import it.xtreamdev.gflbe.dto.purchasecontent.FindContentPurchaseDTO;
import it.xtreamdev.gflbe.model.ContentPurchase;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.repository.ContentPurchaseRepository;
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
import java.util.stream.Collectors;

@Service
public class ContentPurchaseService {

    @Autowired
    private ContentPurchaseRepository contentPurchaseRepository;

    @Autowired
    private NewspaperService newspaperService;

    public Page<ContentPurchase> findContentPurchase(FindContentPurchaseDTO findContentPurchaseDTO, PageRequest pageRequest) {
        return this.contentPurchaseRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(StringUtils.isNotBlank(findContentPurchaseDTO.getGlobalSearch())){
                Arrays.asList(findContentPurchaseDTO.getGlobalSearch().split(" ")).forEach(searchPortion ->
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("note")), "%" + searchPortion.toUpperCase() + "%"))
                );
            }

            if (findContentPurchaseDTO.getNewspaperId() != null) {
                Newspaper newspaper = this.newspaperService.findById(findContentPurchaseDTO.getNewspaperId());
                predicates.add(criteriaBuilder.isMember(newspaper, root.get("newspapers")));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }), pageRequest);
    }

    public ContentPurchase findById(Integer id) {
        return this.contentPurchaseRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
    }

    public ContentPurchase save(SaveContentPurchaseDTO saveContentPurchaseDTO) {
        return this.contentPurchaseRepository.save(ContentPurchase
                .builder()
                .contentNumber(saveContentPurchaseDTO.getContentNumber())
                .note(saveContentPurchaseDTO.getNote())
                .amount(saveContentPurchaseDTO.getAmount())
                .newspapers(saveContentPurchaseDTO.getNewspapers().stream().map(integer -> this.newspaperService.findById(integer)).collect(Collectors.toList()))
                .build());
    }

    public ContentPurchase update(Integer id, SaveContentPurchaseDTO saveContentPurchaseDTO) {
        ContentPurchase contentPurchaseToUpdate = this.findById(id);

        contentPurchaseToUpdate.getNewspapers().clear();

        contentPurchaseToUpdate.setContentNumber(saveContentPurchaseDTO.getContentNumber());
        contentPurchaseToUpdate.setAmount(saveContentPurchaseDTO.getAmount());
        contentPurchaseToUpdate.getNewspapers().addAll(saveContentPurchaseDTO.getNewspapers().stream().map(integer -> this.newspaperService.findById(integer)).collect(Collectors.toList()));
        contentPurchaseToUpdate.setNote(saveContentPurchaseDTO.getNote());

        return this.contentPurchaseRepository.save(contentPurchaseToUpdate);
    }

    public void delete(Integer id) {
        this.contentPurchaseRepository.deleteById(id);
    }


}
