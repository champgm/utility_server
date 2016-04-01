package com.cgm.java.console.commands.implementations;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nullable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.cgm.java.console.commands.Command;
import com.cgm.java.email.GMailSender;

/**
 * Created by mc023219 on 4/1/16.
 */
public class SendEmailCommand extends Command {

    @Override
    protected int run(final CommandLine commandLine) throws Exception {
        final String configFile = commandLine.getOptionValue("configFile");
        final Properties emailProperties = initializeConfiguration(configFile);
        final String userName = emailProperties.getProperty("username");

        final String subject = commandLine.hasOption("subject") ? commandLine.getOptionValue("subject") : "";
        final String body = commandLine.hasOption("body") ? commandLine.getOptionValue("body") : "";


        if (!commandLine.hasOption("to")) {
            usage();
            throw new RuntimeException("You must specify a recipient.");
        }
        final String recipient = commandLine.getOptionValue("to");

        System.out.println("Sender: " + userName);
        System.out.println("Recipient: " + recipient);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);

        GMailSender.sendGMail(emailProperties, userName, subject, body, recipient);
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
