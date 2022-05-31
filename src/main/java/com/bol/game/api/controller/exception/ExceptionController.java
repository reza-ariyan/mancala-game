package com.bol.game.api.controller.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest request, Exception exception) {
        log.error("An error occurred\r\nURL: {} \r\nException: {}]", request.getRequestURL(), exception.getMessage(), exception);

        var modelAndView = new ModelAndView();
        modelAndView.addObject("exception", exception.getMessage());
        modelAndView.setViewName("error");
        return modelAndView;
    }
}