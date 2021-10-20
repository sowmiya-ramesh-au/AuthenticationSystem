package com.ganeshan.authenticationsystem.service;

import com.ganeshan.authenticationsystem.model.Token;
import com.ganeshan.authenticationsystem.repository.TokenRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


@Service
public class DefaultTokenService implements TokenService {

    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);
    private static final Charset US_ASCII = StandardCharsets.US_ASCII;

    @Autowired
    private TokenRepository tokenRepository;

    @Value("${email.token.validity}")
    private int tokenValidityInSeconds;

    @Override
    public Token createToken() {
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()));
        Token token = new Token();
        token.setToken(tokenValue);
        token.setExpireAt(LocalDateTime.now().plusSeconds(tokenValidityInSeconds));
        this.saveToken(token);
        return token;
    }

    @Override
    public void saveToken(Token token) {
        tokenRepository.save(token);
    }

    @Override
    public void removeToken(Token token) {
        tokenRepository.delete(token);
    }

    @Override
    public void removeByToken(String token) {
        tokenRepository.removeByToken(token);
    }


    @Override
    public Token findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
