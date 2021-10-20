package com.ganeshan.authenticationsystem.service;

import com.ganeshan.authenticationsystem.exception.InvalidTokenException;
import com.ganeshan.authenticationsystem.exception.UserAlreadyExistException;
import com.ganeshan.authenticationsystem.model.UserData;

public interface UserService {

    void register(final UserData userData) throws UserAlreadyExistException;

    boolean checkIfUserExist(final String username);

    boolean checkIfEmailExist(final String email);

    boolean verifyUser(String token) throws InvalidTokenException;
}
