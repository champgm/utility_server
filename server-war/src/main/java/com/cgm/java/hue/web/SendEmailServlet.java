package com.cgm.java.hue.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/SendEmailServlet")
public class SendEmailServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailServlet.class);
    private static final long serialVersionUID = 2L;

    public SendEmailServlet() {
        super();
    }

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");

        // Actual logic goes here.
        final PrintWriter out = response.getWriter();
        out.println("<h1>" + "It works?" + "</h1>");

        final String radioButton = request.getParameter("recipient");
        out.println("radioButton ::" + radioButton);

    }

    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
