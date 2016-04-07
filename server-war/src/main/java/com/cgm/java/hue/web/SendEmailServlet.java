package com.cgm.java.hue.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgm.java.email.GMailSender;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

@WebServlet("/SendEmailServlet")
public class SendEmailServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailServlet.class);
    private static final long serialVersionUID = 2L;

    public SendEmailServlet() {
        super();
    }

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final PrintWriter out = response.getWriter();
        try {
            response.setContentType("text/html");
            printNFlush("<h1>" + "Sending an Email..." + "</h1>", out);

            final String recipient = request.getParameter("recipient");
            Preconditions.checkNotNull(recipient, "recipient must be specified.");
            printNFlush("You selected, \"" + recipient + "\"<br>", out);

            ImmutableSet<String> recipients;
            final Properties properties = initializeConfiguration();
            if ("all".equalsIgnoreCase(recipient)) {
                final ImmutableSet.Builder<String> configuredAddressesBuilder = ImmutableSet.builder();
                for (final Object keyObject : properties.keySet()) {
                    final String key = (String) keyObject;
                    if (!"username".equalsIgnoreCase(key) && !"password".equalsIgnoreCase(key)) {
                        configuredAddressesBuilder.add(properties.getProperty(key));
                        printNFlush("Found an address for, \"" + key + "\"<br>", out);

                    }
                }

                recipients = configuredAddressesBuilder.build();
                Preconditions.checkState(!recipients.isEmpty(), "No addresses found in configuration.");
            } else {
                final String recipientAddress = properties.getProperty(recipient);
                if (recipientAddress == null) {
                    printNFlush("Recipient not found: " + recipient + "<br>" + "Only found these: <br>", out);
                    for (final Object o : properties.keySet()) {
                        printNFlush(o.toString() + "<br>", out);
                    }
                }
                Preconditions.checkNotNull(recipientAddress, "No addresses found in configuration for recipient, \"" + recipient + "\".");
                recipients = ImmutableSet.of(recipientAddress);

                printNFlush("Found an address for, \"" + recipient + "\"<br>", out);
            }

            final String inputSubject = request.getParameter("subject");
            final String subject = inputSubject == null ? "" : inputSubject;
            final String inputBody = request.getParameter("body");
            final String body = inputBody == null ? "" : inputBody;

            printNFlush("Attempting to send an email...<br>", out);
            GMailSender.sendGMail(properties, Optional.absent(), subject, body, recipients);
            printNFlush("Done with send attempt.<br>", out);
        } catch (Exception e) {
            printNFlush("An error seems to have occurred.<br><br>", out);
            printNFlush(e.getMessage() + "<br>", out);
            for (final StackTraceElement stackTraceElement : e.getStackTrace()) {
                printNFlush(stackTraceElement.toString() + "<br>", out);
            }

        }
    }

    private void printNFlush(final String message, final PrintWriter out) {
        out.print(message);
        out.flush();
    }

    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private Properties initializeConfiguration() {
        final String fileName = "email.properties";

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
