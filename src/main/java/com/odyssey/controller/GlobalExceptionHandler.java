package com.odyssey.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(XMLStreamException.class)
    @ResponseBody
     String handleXMLException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "An error occurred during the XML parsing!";
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    String handleIOExceptions(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return "The file is invalid!";
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseBody
    String handleDBException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return "Error! There\\'s no order with such ID!";
    }
}
