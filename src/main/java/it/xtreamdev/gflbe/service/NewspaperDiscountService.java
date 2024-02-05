package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.newspaperdiscount.SaveNewspaperDiscount;
import it.xtreamdev.gflbe.dto.newspaperdiscount.SearchNewspaperDiscount;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.model.NewspaperDiscount;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.NewspaperDiscountRepository;
import it.xtreamdev.gflbe.repository.NewspaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewspaperDiscountService {

    @Autowired
    private NewspaperRepository newspaperRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NewspaperDiscountRepository newspaperDiscountRepository;

    public NewspaperDiscount save(SaveNewspaperDiscount saveNewspaperDiscount) {
        User customer = this.userService.findByIdAndRole(saveNewspaperDiscount.getCustomerId(), RoleName.CUSTOMER);
        Newspaper newspaper = null;
        if (saveNewspaperDiscount.getNewspaperId() != null) {
            newspaper = this.newspaperRepository.findById(saveNewspaperDiscount.getNewspaperId()).orElseThrow();
        }

        return this.newspaperDiscountRepository.save(NewspaperDiscount.builder()
                .customer(customer)
                .newspaper(newspaper)
                .allNewspaper(saveNewspaperDiscount.getAllNewspaper())
                .discountPercentage(saveNewspaperDiscount.getDiscountPercentage())
                .build());
    }

    public NewspaperDiscount update(Integer id, SaveNewspaperDiscount saveNewspaperDiscount) {
        User customer = this.userService.findByIdAndRole(saveNewspaperDiscount.getCustomerId(), RoleName.CUSTOMER);
        Newspaper newspaper = null;
        if (saveNewspaperDiscount.getNewspaperId() != null) {
            newspaper = this.newspaperRepository.findById(saveNewspaperDiscount.getNewspaperId()).orElseThrow();
        }


        NewspaperDiscount newspaperDiscount = this.newspaperDiscountRepository.findById(id).orElseThrow();
        newspaperDiscount.setDiscountPercentage(saveNewspaperDiscount.getDiscountPercentage());
        newspaperDiscount.setAllNewspaper(saveNewspaperDiscount.getAllNewspaper());
        newspaperDiscount.setCustomer(customer);
        newspaperDiscount.setNewspaper(newspaper);

        return this.newspaperDiscountRepository.save(newspaperDiscount);
    }

    public void delete(Integer id) {
        this.newspaperDiscountRepository.deleteById(id);
    }

    public NewspaperDiscount findById(Integer id) {
        NewspaperDiscount newspaperDiscount = this.newspaperDiscountRepository.findById(id).orElseThrow();
        User currentUser = userService.userInfo();
        if ((currentUser.getRole() == RoleName.ADMIN) || (currentUser.getRole() == RoleName.CUSTOMER && newspaperDiscount.getCustomer().getId().equals(currentUser.getId()))) {
            return newspaperDiscount;
        }

        throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
    }

    public Page<NewspaperDiscount> find(SearchNewspaperDiscount searchNewspaperDiscount, PageRequest pageRequest) {
        User currentUser = userService.userInfo();
        return this.newspaperDiscountRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchNewspaperDiscount.getNewspaperId() != null) {
                Newspaper newspaper = this.newspaperRepository.findById(searchNewspaperDiscount.getNewspaperId()).orElseThrow();
                predicates.add(criteriaBuilder.equal(root.get("newspaper"), newspaper));
            }

            if (currentUser.getRole() == RoleName.CUSTOMER) {
                predicates.add(criteriaBuilder.equal(root.get("customer"), currentUser));
            } else if (searchNewspaperDiscount.getCustomerId() != null) {
                User customer = this.userService.findByIdAndRole(searchNewspaperDiscount.getCustomerId(), RoleName.CUSTOMER);
                predicates.add(criteriaBuilder.equal(root.get("customer"), customer));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);
    }

}
