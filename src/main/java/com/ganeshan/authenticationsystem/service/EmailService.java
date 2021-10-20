package com.ganeshan.authenticationsystem.service;

import com.ganeshan.authenticationsystem.email.AbstractEmailContext;

import javax.mail.MessagingException;

public interface EmailService {
    void sendMail(final AbstractEmailContext email) throws MessagingException;
}
