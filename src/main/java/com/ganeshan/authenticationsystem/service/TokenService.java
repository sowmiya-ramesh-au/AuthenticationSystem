package com.ganeshan.authenticationsystem.service;

import com.ganeshan.authenticationsystem.model.Token;

public interface TokenService {
    Token createToken();

    void saveToken(Token token);

    void removeToken(Token token);

    void removeByToken(String token);

    Token findByToken(String token);
}
