package com.cgm.java.email;

import java.util.Collection;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class GMailSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(GMailSender.class);

    public static void sendGMail(
            final Properties emailProperties,
            final Optional<String> inputUserName,
            final String subject,
            final String body,
            final Collection<String> recipients) {
        Preconditions.checkNotNull(emailProperties, "emailProperties may not be null.");
        Preconditions.checkNotNull(inputUserName, "userName may not be null.");
        Preconditions.checkNotNull(subject, "subject may not be null.");
        Preconditions.checkNotNull(body, "body may not be null.");
        Preconditions.checkNotNull(recipients, "recipient may not be null.");
        final String userName = inputUserName.isPresent() ? inputUserName.get() : emailProperties.getProperty("username");
        Preconditions.checkNotNull(inputUserName, "Could not retrieve username from config.");

        LOGGER.info("Sender: " + userName);
        LOGGER.info("Recipient: " + recipients.toString());
        LOGGER.info("Subject: " + subject);
        LOGGER.info("Body: " + body);

        final Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        final Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                final String password = emailProperties.getProperty("password");
                return new PasswordAuthentication(userName, password);
            }
        });

        try {
            for (final String recipient : recipients) {
                final Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(userName));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(subject);
                message.setText(body);
                Transport.send(message);
                LOGGER.info("Email sent to, \"" + recipient + "\"");
            }

            LOGGER.info("Success.");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
