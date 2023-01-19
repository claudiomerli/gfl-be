package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.AccessTokenDTO;
import it.xtreamdev.gflbe.dto.user.EditUserDTO;
import it.xtreamdev.gflbe.dto.user.SaveUserDTO;
import it.xtreamdev.gflbe.dto.SigninDTO;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.UserRepository;
import it.xtreamdev.gflbe.security.JwtTokenUtil;
import it.xtreamdev.gflbe.security.model.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public AccessTokenDTO signin(SigninDTO signinDTO) {
        User user = this.userRepository.findByUsername(signinDTO.getUsername()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Wrong credentials"));

        if (!this.passwordEncoder.matches(signinDTO.getPassword(), user.getPassword())) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Wrong credentials");
        }

        String token = this.jwtTokenUtil.generateToken(user);
        return AccessTokenDTO.builder().accessToken(token).build();
    }

    public User findByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Username not found"));
    }

    public Page<User> findUsers(String gloabalSearch, String role, Pageable pageable) {
        return this.userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(role)) {
                predicates.add(criteriaBuilder.equal(root.get("role"), RoleName.valueOf(role)));
            }

            if (StringUtils.isNotBlank(gloabalSearch)) {
                Arrays.asList(gloabalSearch.split(" ")).forEach(searchPortion -> {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("fullname")), "%" + searchPortion.toUpperCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("email")), "%" + searchPortion.toUpperCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("username")), "%" + searchPortion.toUpperCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("mobilePhone")), "%" + searchPortion.toUpperCase() + "%")
                    ));
                });
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Transactional
    public void createUser(SaveUserDTO editsaveUserDTO) {
        this.userRepository.findByUsername(editsaveUserDTO.getUsername()).ifPresent((user) -> {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Username already exists");
        });

        User user = User
                .builder()
                .username(editsaveUserDTO.getUsername())
                .fullname(editsaveUserDTO.getFullname())
                .email(editsaveUserDTO.getEmail())
                .mobilePhone(editsaveUserDTO.getMobilePhone())
                .remuneration(editsaveUserDTO.getRemuneration())
                .level(editsaveUserDTO.getLevel())
                .role(editsaveUserDTO.getRole())
                .password(this.passwordEncoder.encode(editsaveUserDTO.getPassword()))
                .build();

        this.userRepository.save(user);
    }

    public User updateUser(Integer id, EditUserDTO userUpdated) {
        User userFromDB = this.userRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found"));

        userFromDB.setEmail(userUpdated.getEmail());
        userFromDB.setFullname(userUpdated.getFullname());
        userFromDB.setMobilePhone(userUpdated.getMobilePhone());
        userFromDB.setLevel(userUpdated.getLevel());
        userFromDB.setRemuneration(userUpdated.getRemuneration());
        userFromDB.setRole(userUpdated.getRole());

        if (StringUtils.isNotBlank(userUpdated.getPassword())) {
            userFromDB.setPassword(this.passwordEncoder.encode(userUpdated.getPassword()));
        }

        return this.userRepository.save(userFromDB);
    }

    public void deleteUser(Integer id) {
        this.userRepository.deleteById(id);
    }

    public User findById(Integer id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found"));
    }

    public User userInfo() {
        return ((JwtUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }
}
