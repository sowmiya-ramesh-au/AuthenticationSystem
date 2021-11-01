package com.ganeshan.authenticationsystem.service;


import com.ganeshan.authenticationsystem.email.AccountVerificationEmailContext;
import com.ganeshan.authenticationsystem.exception.InvalidTokenException;
import com.ganeshan.authenticationsystem.exception.UserAlreadyExistException;
import com.ganeshan.authenticationsystem.model.Token;
import com.ganeshan.authenticationsystem.model.UserData;
import com.ganeshan.authenticationsystem.model.UserEntity;
import com.ganeshan.authenticationsystem.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Objects;
import java.util.Optional;


@Service("userService")
public class DefaultUserService implements UserService {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailService emailService;

    @Value("${site.base.url}")
    private String baseURL;

    @Override
    public void processOAuthPostLogin(String username, String email) {
        UserEntity userEntity = null;
        if (checkIfEmailExist(email)) {
            if (!checkIfUserExist(username)) {
                userEntity = getUserByEmail(email);
                userEntity.setUsername(username);
                userEntity.setVerified(true);
            }
        } else {
            userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setEmail(email);
            userEntity.setVerified(true);
        }

        if (userEntity != null) {
            userRepository.save(userEntity);
        }
    }


    @Override
    public void register(UserData userData) throws UserAlreadyExistException {
        if (checkIfEmailExist(userData.getEmail())) {
            throw new UserAlreadyExistException("Email already available");
        }
        if (checkIfUserExist(userData.getUsername())) {
            throw new UserAlreadyExistException("Username already available");
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userData, userEntity);
        encodePassword(userData, userEntity);
        userRepository.save(userEntity);
        sendRegistrationConfirmationEmail(userEntity);
    }

    private void encodePassword(UserData userData, UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userData.getPassword()));
    }

    @Override
    public boolean checkIfUserExist(String username) {
        return userRepository.usernameExists(username) != null;
    }

    @Override
    public boolean checkIfEmailExist(String email) {
        return userRepository.emailExists(email) != null;
    }


    public void sendRegistrationConfirmationEmail(UserEntity userEntity) {
        Token token = tokenService.createToken();
        token.setUser(userEntity);
        tokenService.saveToken(token);
        AccountVerificationEmailContext emailContext = new AccountVerificationEmailContext();
        emailContext.init(userEntity);
        emailContext.setToken(token.getToken());
        emailContext.buildVerificationUrl(baseURL, token.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean verifyUser(String userToken) throws InvalidTokenException {
        Token storedToken = tokenService.findByToken(userToken);
//        TODO:Check expire condition of given token
        if (Objects.isNull(storedToken) || !StringUtils.equals(userToken, storedToken.getToken())) {
            throw new InvalidTokenException("Token is not valid");
        }
        Optional<UserEntity> optionalEntity = userRepository.findById(storedToken.getUser().getId());
        if (optionalEntity.isEmpty()) {
            return false;
        }
        UserEntity userEntity = optionalEntity.get();
        userEntity.setVerified(true);
        userRepository.save(userEntity);
        tokenService.removeToken(storedToken);
        return true;
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return userRepository.usernameExists(username);
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.emailExists(email);
    }
}
