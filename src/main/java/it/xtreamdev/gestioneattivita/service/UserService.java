package it.xtreamdev.gestioneattivita.service;

import it.xtreamdev.gestioneattivita.model.User;
import it.xtreamdev.gestioneattivita.model.enumerations.RoleName;
import it.xtreamdev.gestioneattivita.repository.UserRepository;
import it.xtreamdev.gestioneattivita.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(s).orElseThrow(() -> new UsernameNotFoundException(s));
        return new UserPrincipal(user);
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

    public void validateCreateEditor(User user, BindingResult bindingResult) {
        this.userRepository.findByUsername(user.getUsername()).ifPresent(userFound -> {
            bindingResult.addError(new FieldError("editor", "username", "Username already exists"));
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
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal.getUser();
    }
}
