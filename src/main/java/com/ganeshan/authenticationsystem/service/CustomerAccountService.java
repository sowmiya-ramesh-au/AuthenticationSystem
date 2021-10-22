package com.ganeshan.authenticationsystem.service;

import com.ganeshan.authenticationsystem.exception.InvalidTokenException;
import com.ganeshan.authenticationsystem.exception.UnknownIdentifierException;

public interface CustomerAccountService {
    void forgottenPassword(String username) throws UnknownIdentifierException;

    void updatePassword(String password, String token) throws InvalidTokenException, UnknownIdentifierException;
}
