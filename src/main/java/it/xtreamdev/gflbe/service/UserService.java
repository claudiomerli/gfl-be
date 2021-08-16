package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.AccessTokenDto;
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

    public Page<User> findEditors(Pageable pageable) {
        return this.userRepository.findByRole(RoleName.EDITOR, pageable);
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

    public User createEditor(User user) {

        user.setRole(RoleName.EDITOR);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        return this.userRepository.save(user);
    }

    public User updateEditor(Integer id, User userUpdated) {
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

    public void delete(Integer id) {
        this.userRepository.deleteById(id);
    }

    public User findById(Integer id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found"));
    }

    public User currentUserAuthentication() {
        JwtUserPrincipal userPrincipal = (JwtUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal.getUser();
    }
}
