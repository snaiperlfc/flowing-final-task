package org.zimin.shipping.service;

import org.springframework.core.io.InputStreamSource;

import javax.mail.MessagingException;

public interface EmailService {

    void sendSimpleMessage(String to, String subject, String text);

    void sendMessageWithAttachment(String to, String subject, String text, String attachmentName,
                                   InputStreamSource pathToAttachment) throws MessagingException;
}
