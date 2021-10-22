package com.ganeshan.authenticationsystem.service;

import com.ganeshan.authenticationsystem.email.ForgotPasswordEmailContext;
import com.ganeshan.authenticationsystem.exception.InvalidTokenException;
import com.ganeshan.authenticationsystem.exception.UnknownIdentifierException;
import com.ganeshan.authenticationsystem.model.Token;
import com.ganeshan.authenticationsystem.model.UserEntity;
import com.ganeshan.authenticationsystem.repository.TokenRepository;
import com.ganeshan.authenticationsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.mail.MessagingException;
import java.util.Objects;
import java.util.Optional;

@Service
public class DefaultCustomerAccountService implements CustomerAccountService {

    @Autowired
    TokenService tokenService;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${site.base.url}")
    private String baseURL;

    @Override
    public void forgottenPassword(String username) throws UnknownIdentifierException {
        UserEntity userEntity = userService.getUserByEmail(username);
        sendResetPasswordEmail(userEntity);
    }

    @Override
    public void updatePassword(String password, String token) throws InvalidTokenException, UnknownIdentifierException {
        Token tokenData = tokenService.findByToken(token);
        if (Objects.isNull(tokenData) || !StringUtils.equals(token, tokenData.getToken()) || tokenData.isExpired()) {
            throw new InvalidTokenException("Token is not valid");
        }
        Optional<UserEntity> user = userRepository.findById(tokenData.getUser().getId());
        if (user.isEmpty()) {
            throw new UnknownIdentifierException("Unable to find user for the token");
        }
        tokenService.removeToken(tokenData);
        user.get().setPassword(passwordEncoder.encode(password));
        userRepository.save(user.get());
    }

    protected void sendResetPasswordEmail(UserEntity userEntity) {
        Token token = tokenService.createToken();
        token.setUser(userEntity);
        tokenRepository.save(token);
        ForgotPasswordEmailContext emailContext = new ForgotPasswordEmailContext();
        emailContext.init(userEntity);
        emailContext.setToken(token.getToken());
        emailContext.buildVerificationUrl(baseURL, token.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException exception) {
            exception.printStackTrace();
        }
    }
}
