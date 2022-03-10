package com.iv1201.recruiterwebapp.recruiterwebapp.controller;

import com.iv1201.recruiterwebapp.recruiterwebapp.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.ConnectException;
import java.net.URISyntaxException;

/**
 * This class catches all exceptions.
 */
@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {
    private final ClientRepository clientRepository;
    @Value("${client.id-cookie.name}")
    private String tokenName;

    /**
     * Constructor for exception handler.
     * @param clientRepository Repo to access client template.
     */
    @Autowired
    public GlobalControllerExceptionHandler(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * To handle any authorization exception.
     * @param req Http request to access cookies.
     * @param unauthorizedError The error to catch.
     * @return Login page with error parameter.
     */
    @ExceptionHandler({HttpClientErrorException.class})
    public String handleUnauthorizedException(HttpServletRequest req, Exception unauthorizedError) {
        if(unauthorizedError instanceof HttpClientErrorException){
            HttpClientErrorException httpClientErrorException = (HttpClientErrorException) unauthorizedError;
            log.error("Ex: " + httpClientErrorException);
        }
        for (Cookie c : req.getCookies()){
            if(c.getName().equals(tokenName)){
                clientRepository.delete(c.getValue());
                log.error("Ex: " + unauthorizedError);
                return "redirect:/login?error=true";
            }
        }
        log.error("Ex: " + unauthorizedError);
        return "redirect:/login?error=true";
    }

    /**
     * Handle exceptions thrown when backend server is not running.
     * @param error Error to catch.
     * @param redirectAttributes To send message with redirect.
     * @return Redirect to error page.
     */
    @ExceptionHandler({ResourceAccessException.class})
    public String handleConnectionToBackendException(Exception error, RedirectAttributes redirectAttributes) {
        log.error("Ex: " + error);
        //System.out.println(error.getMessage());
        //System.out.println(error.getCause());
        String m = "Connection to server is lost";
        redirectAttributes.addAttribute("message", m);
        return "redirect:/error";
    }


    /**
     * Handle exception when trying to parse a uri that doesn't have the correct format.
     *  //TODO: find correct class
     * @param error Error to catch.
     * @param redirectAttributes To send message with redirect.
     * @return Redirect to error page.
     */
    @ExceptionHandler({URISyntaxException.class})
    public String handleBadURISyntaxException(Exception error, RedirectAttributes redirectAttributes) {
        log.error("Ex: " + error);
        //System.out.println(error.getMessage());
        //System.out.println(error.getCause());
        String m = "Connection to server is lost"; //URISyntaxException
        redirectAttributes.addAttribute("message", m);
        return "redirect:/error";
    }

    /**
     * To handle any general exception that is not handled in any other handler.
     * @param req
     * @param error Error to catch.
     * @param redirectAttributes To send message with redirect.
     * @return Redirect to error page.
     */
    @ExceptionHandler({Exception.class})
    public String handle(HttpServletRequest req, Exception error, RedirectAttributes redirectAttributes) {
        log.error("Ex: " + error);
        //System.out.println(error.getMessage());
        System.out.println(error.getMessage());
        System.out.println(error.getLocalizedMessage());
        System.out.println(error.getClass());
        System.out.println(error.getStackTrace());
        String m = "Something went wrong";
        redirectAttributes.addAttribute("message", m);
        return "redirect:/error";
    }
}



