package com.cgm.java.hue.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgm.java.hue.web.util.KnownParameterNames;

@WebServlet("/HueServlet")
public class SendEmailServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailServlet.class);
    private static final long serialVersionUID = 2L;

    public SendEmailServlet() {
        super();
    }

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        // Grab the input scene ID and retrieve that scene from the bridge
        final String sceneId = request.getParameter(KnownParameterNames.SCENE_ID.getName());
        LOGGER.info("Attempting to activate scene with ID: " + sceneId);

        // Return the scene
    }

    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
