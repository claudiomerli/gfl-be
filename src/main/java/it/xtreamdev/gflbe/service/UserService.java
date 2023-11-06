package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.auth.*;
import it.xtreamdev.gflbe.dto.user.EditUserDTO;
import it.xtreamdev.gflbe.dto.user.SaveUserDTO;
import it.xtreamdev.gflbe.model.CustomerInfo;
import it.xtreamdev.gflbe.model.EditorInfo;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.UserRepository;
import it.xtreamdev.gflbe.security.JwtTokenUtil;
import it.xtreamdev.gflbe.security.model.JwtUserPrincipal;
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
import java.util.*;

import static it.xtreamdev.gflbe.util.NetUtils.extractDomain;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

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
                .role(editsaveUserDTO.getRole())
                .active(true)
                .password(this.passwordEncoder.encode(editsaveUserDTO.getPassword()))
                .build();

        user.setCustomerInfo(CustomerInfo.builder().customer(user).build());
        user.setEditorInfo(EditorInfo
                .builder()
                .remuneration(editsaveUserDTO.getEditorInfoRemuneration())
                .info(editsaveUserDTO.getEditorInfo())
                .notes(editsaveUserDTO.getEditorInfoNotes())
                .editor(user)
                .build());

        this.userRepository.save(user);
    }

    public User updateUser(Integer id, EditUserDTO userUpdated) {
        User userFromDB = this.userRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found"));

        userFromDB.setEmail(userUpdated.getEmail());
        userFromDB.setFullname(userUpdated.getFullname());
        userFromDB.setMobilePhone(userUpdated.getMobilePhone());
        userFromDB.setRole(userUpdated.getRole());

        userFromDB.getEditorInfo().setRemuneration(userUpdated.getEditorInfoRemuneration());
        userFromDB.getEditorInfo().setInfo(userUpdated.getEditorInfo());
        userFromDB.getEditorInfo().setNotes(userUpdated.getEditorInfoNotes());

        userFromDB.getCustomerInfo().setCompanyName(userUpdated.getCompanyName());
        userFromDB.getCustomerInfo().setUrl(userUpdated.getUrl());
        userFromDB.getCustomerInfo().setCompanyDimension(userUpdated.getCompanyDimension());
        userFromDB.getCustomerInfo().setBusinessArea(userUpdated.getBusinessArea());
        userFromDB.getCustomerInfo().setAddress(userUpdated.getAddress());
        userFromDB.getCustomerInfo().setCompetitor1(userUpdated.getCompetitor1());
        userFromDB.getCustomerInfo().setCompetitor2(userUpdated.getCompetitor2());
        userFromDB.getCustomerInfo().setPrincipalDomain(userUpdated.getPrincipalDomain());

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

    public User findByIdCleaned(Integer id) {
        return this.findById(id).cleanSensitiveData();
    }

    public User userInfo() {
        return ((JwtUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public void createMissingObjects() {
        List<User> userWithMissingEditorInfo = this.userRepository.findByEditorInfoIsNull();
        userWithMissingEditorInfo.forEach(user -> {
            user.setEditorInfo(EditorInfo.builder().editor(user).build());
            this.userRepository.save(user);
        });
    }

    public AccessTokenDTO signinCustomer(SigninDTO signinDTO) {
        User user = this.userRepository.findByUsernameAndRoleInAndActiveTrue(signinDTO.getUsername(), List.of(RoleName.CUSTOMER, RoleName.FINAL_CUSTOMER)).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Wrong credentials"));

        if (!this.passwordEncoder.matches(signinDTO.getPassword(), user.getPassword())) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Wrong credentials");
        }

        String token = this.jwtTokenUtil.generateToken(user);

        return AccessTokenDTO.builder().accessToken(token).build();
    }

    public void checkUsernameAvailability(String username) {
        Optional<User> user = this.userRepository.findByUsername(username);
        if (user.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Username exists");
        }
    }

    public void checkEmailAvailability(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Username exists");
        }
    }

    @Transactional
    public void createCustomer(SaveCustomerDTO saveCustomerDTO) {
        String username = saveCustomerDTO.getUsername();
        String email = saveCustomerDTO.getEmail();
        this.checkUsernameAvailability(username);
        this.checkUsernameAvailability(email);

        User userToSave = User.builder()
                .username(username)
                .password(passwordEncoder.encode(saveCustomerDTO.getPassword()))
                .email(email)
                .fullname(saveCustomerDTO.getCompanyName())
                .mobilePhone(saveCustomerDTO.getMobile())
                .activationCode(UUID.randomUUID().toString())
                .role(RoleName.CUSTOMER)
                .build();

        userToSave.setCustomerInfo(CustomerInfo.builder()
                .customer(userToSave)
                .companyName(saveCustomerDTO.getCompanyName())
                .address(saveCustomerDTO.getAddress())
                .businessArea(saveCustomerDTO.getBusinessArea())
                .companyDimension(saveCustomerDTO.getCompanyDimension())
                .url(extractDomain(saveCustomerDTO.getUrl()))
                .build());

        User savedUser = this.userRepository.save(userToSave);
        this.mailService.sendSignupMail(savedUser);
    }

    public void confirm(String code) {
        User user = this.userRepository.findByActivationCode(code).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found"));
        user.setActive(true);
        user.setActivationCode(null);
        this.userRepository.save(user);
    }

    public void confirmEmail(String code) {
        User user = this.userRepository.findByEmailVerificationCode(code).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found"));
        user.setEmailVerified(true);
        user.setEmailVerificationCode(null);
        this.userRepository.save(user);
    }

    public void updateCustomerInfo(Integer id, SaveCustomerInfoDTO saveCustomerDTO) {
        User user = this.findById(id);
        user.getCustomerInfo().setCompanyName(saveCustomerDTO.getCompanyName());
        user.getCustomerInfo().setCompanyDimension(saveCustomerDTO.getCompanyDimension());
        user.getCustomerInfo().setBusinessArea(saveCustomerDTO.getBusinessArea());
        user.getCustomerInfo().setAddress(saveCustomerDTO.getAddress());
        user.setMobilePhone(saveCustomerDTO.getMobile());

        this.userRepository.save(user);
    }


    @Transactional
    public void updateCustomerEmail(Integer id, SaveCustomerEmailDTO saveCustomerDTO) {
        this.checkEmailAvailability(saveCustomerDTO.getEmail());

        User user = this.findById(id);
        user.setEmail(saveCustomerDTO.getEmail());
        user.setEmailVerified(false);
        user.setEmailVerificationCode(UUID.randomUUID().toString());
        this.userRepository.save(user);

        this.mailService.sendChangeEmailMail(user);
    }

    public void updateCustomerPassword(Integer id, SaveCustomerPasswordDTO saveCustomerDTO) {
        User user = this.findById(id);
        if (!this.passwordEncoder.matches(saveCustomerDTO.getOldPassword(), user.getPassword())) {
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Old password incorrect");
        }

        user.setPassword(this.passwordEncoder.encode(saveCustomerDTO.getPassword()));
        this.userRepository.save(user);
    }

    public void updateCustomerCompetitors(Integer id, SaveCustomerStatisticDomainDTO saveCustomerStatisticDomainDTO) {
        User user = this.findById(id);
        User currentUser = userInfo();

        if(currentUser.getRole() == RoleName.CUSTOMER && !currentUser.getId().equals(user.getId())){
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Cannot update another customer");
        }

//        if (currentUser.getRole() == RoleName.CUSTOMER) {
//            if (!currentUser.getId().equals(user.getId())
//                    ||
//                    StringUtils.isNotBlank(saveCustomerCompetitorsDTO.getCompetitor1()) && StringUtils.isNotBlank(user.getCustomerInfo().getCompetitor1())
//                    ||
//                    StringUtils.isNotBlank(saveCustomerCompetitorsDTO.getCompetitor2()) && StringUtils.isNotBlank(user.getCustomerInfo().getCompetitor2())
//            ) {
//                throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Cannot update already saved competitor");
//            }
//        }
//
//        if (StringUtils.isNotBlank(saveCustomerCompetitorsDTO.getCompetitor1())) {
//            user.getCustomerInfo().setCompetitor1(extractDomain(saveCustomerCompetitorsDTO.getCompetitor1()));
//        }
//
//        if (StringUtils.isNotBlank(saveCustomerCompetitorsDTO.getCompetitor2())) {
//            user.getCustomerInfo().setCompetitor2(extractDomain(saveCustomerCompetitorsDTO.getCompetitor2()));
//        }

        user.getCustomerInfo().setPrincipalDomain(extractDomain(saveCustomerStatisticDomainDTO.getPrincipalDomain()));
        user.getCustomerInfo().setCompetitor1(extractDomain(saveCustomerStatisticDomainDTO.getCompetitor1()));
        user.getCustomerInfo().setCompetitor2(extractDomain(saveCustomerStatisticDomainDTO.getCompetitor2()));


        this.userRepository.save(user);
    }

    public void requestResetPassword(ResetPasswordEmailDTO resetPasswordEmailDTO) {
        User user = this.userRepository.findByEmail(resetPasswordEmailDTO.getEmail()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found"));
        user.setResetPasswordCode(UUID.randomUUID().toString());
        this.userRepository.save(user);

        this.mailService.sendResetMail(user);
    }

    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        User user = this.userRepository.findByResetPasswordCode(resetPasswordDTO.getCode()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found"));
        user.setPassword(this.passwordEncoder.encode(resetPasswordDTO.getPassword()));
        user.setResetPasswordCode(null);
        this.userRepository.save(user);
    }


}
