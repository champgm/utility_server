package com.cgm.java.console.commands.implementations;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nullable;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.cgm.java.console.commands.Command;

/**
 * Created by mc023219 on 4/1/16.
 */
public class SendEmailCommand extends Command {

    @Override
    protected int run(final CommandLine commandLine) throws Exception {
        final String configFile = commandLine.getOptionValue("configFile");
        final Properties emailProperties = initializeConfiguration(configFile);
        final String userName = emailProperties.getProperty("username");
        System.out.println("Sender: " + userName);

        if (!commandLine.hasOption("to")) {
            usage();
            throw new RuntimeException("You must specify a recipient.");
        }
        final String recipient = commandLine.getOptionValue("to");
        System.out.println("Recipient: " + recipient);

        final Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        final Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                final String password = emailProperties.getProperty("password");
                return new PasswordAuthentication(userName, password);
            }
        });

        try {
            final String subject = commandLine.hasOption("subject") ? commandLine.getOptionValue("subject") : "";
            final String body = commandLine.hasOption("body") ? commandLine.getOptionValue("body") : "";
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);

            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);

            System.out.println();
            System.out.println("Success.");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public String getName() {
        return "sendemail";
    }

    @Override
    public void usage() {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getName(), "A command to send an e-mail.", getOptions(), null);
    }

    @Override
    public Options getOptions() {
        final Options options = super.getOptions();
        options.addOption("to", true, "The address of the recipient.");
        options.addOption("subject", true, "The address of the recipient.");
        options.addOption("body", true, "The address of the recipient.");
        return options;
    }

    private Properties initializeConfiguration(@Nullable final String inputFileName) {
        final String fileName = StringUtils.isBlank(inputFileName) ? "email.properties" : inputFileName;

        InputStream inputStream = null;
        try {
            final Properties emailProperties = new Properties();
            inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

            if (inputStream != null) {
                emailProperties.load(inputStream);
                return emailProperties;
            } else {
                throw new FileNotFoundException("Properties file '" + fileName + "' not found in the classpath");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to import configuration from file: " + fileName);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
