package com.salma.usersmicroservice.util;

public interface EmailSender {
    void sendEmail(String toEmail, String body);
}