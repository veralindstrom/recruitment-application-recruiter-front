package com.iv1201.recruiterwebapp.recruiterwebapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * This controller handles the default error page.
 */
@Controller
public class RecruiterErrorController implements ErrorController {

    /**
     * Constructor for error controller.
     */
    @Autowired
    public RecruiterErrorController() {
    }

    /**
     * Get error page. When something wrong.
     * @return Error page.
     */
    @GetMapping("/error")
    public String error(@ModelAttribute("message") String message){
        return "public/errorPage";
    }
}
