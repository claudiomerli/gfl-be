package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.AccessTokenDto;
import it.xtreamdev.gflbe.dto.editor.EditEditorDTO;
import it.xtreamdev.gflbe.dto.editor.SaveEditorDTO;
import it.xtreamdev.gflbe.dto.SigninDTO;
import it.xtreamdev.gflbe.exception.GLFException;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Predicate;
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

    public AccessTokenDto signin(SigninDTO signinDTO) {
        User user = this.userRepository.findByUsername(signinDTO.getUsername()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Wrong credentials"));

        if (!this.passwordEncoder.matches(signinDTO.getPassword(), user.getPassword())) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Wrong credentials");
        }

        String token = this.jwtTokenUtil.generateToken(user);
        return AccessTokenDto.builder().accessToken(token).build();
    }

    public User findByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Username not found"));
    }

    public Long countEditors() {
        return this.userRepository.countByRole(RoleName.EDITOR);
    }

    public List<User> findEditors() {
        return this.userRepository.findByRole(RoleName.EDITOR);
    }

    public Page<User> findEditors(String gloabalSearch, Pageable pageable) {
        return this.userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("role"), RoleName.EDITOR));

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

    @Deprecated
    public void validateCreateEditor(User user, BindingResult bindingResult) {
        this.userRepository.findByUsername(user.getUsername()).ifPresent(userFound -> {
            bindingResult.addError(new FieldError("editor", "username", "Username already exists"));
        });
    }

    public void validateCreateEditor(User user) {
        this.userRepository.findByUsername(user.getUsername()).ifPresent(userFound -> {
            throw new GLFException("Username already exists", HttpStatus.BAD_REQUEST);
        });
    }

    public void createEditor(SaveEditorDTO editor) {
        this.userRepository.findByUsername(editor.getUsername()).ifPresent((user) -> {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Username already exists");
        });

        User user = User
                .builder()
                .username(editor.getUsername())
                .fullname(editor.getFullname())
                .email(editor.getEmail())
                .mobilePhone(editor.getMobilePhone())
                .remuneration(editor.getRemuneration())
                .level(editor.getLevel())
                .role(RoleName.EDITOR)
                .password(this.passwordEncoder.encode(editor.getPassword()))
                .build();

        this.userRepository.save(user);
    }

    public User updateEditor(Integer id, EditEditorDTO userUpdated) {
        User userFromDB = this.userRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found"));

        userFromDB.setEmail(userUpdated.getEmail());
        userFromDB.setFullname(userUpdated.getFullname());
        userFromDB.setMobilePhone(userUpdated.getMobilePhone());
        userFromDB.setLevel(userUpdated.getLevel());
        userFromDB.setRemuneration(userUpdated.getRemuneration());

        if (StringUtils.isNotBlank(userUpdated.getPassword())) {
            userFromDB.setPassword(this.passwordEncoder.encode(userUpdated.getPassword()));
        }

        return this.userRepository.save(userFromDB);
    }

    public void deleteEditor(Integer id) {
        this.userRepository.deleteByIdAndRole(id, RoleName.EDITOR);
    }

    public User findById(Integer id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found"));

        user.setPassword(null);

        return user;
    }

    public User currentUserAuthentication() {
        JwtUserPrincipal userPrincipal = (JwtUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal.getUser();
    }
}
