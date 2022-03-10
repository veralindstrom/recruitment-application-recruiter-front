package com.iv1201.recruiterwebapp.recruiterwebapp.controller;

import com.iv1201.recruiterwebapp.recruiterwebapp.DTO.ApplicationDTO;
import com.iv1201.recruiterwebapp.recruiterwebapp.model.*;
import com.iv1201.recruiterwebapp.recruiterwebapp.repository.ClientRepository;
import com.iv1201.recruiterwebapp.recruiterwebapp.service.APIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This controller handles login and log out of a user.
 */
@Controller
@Slf4j
public class IndexController {
    private final APIService apiService;
    private final ClientRepository clientRepository;
    private final ArrayList<ApplicationDTO> applicationList = new ArrayList<>();
    private final Applicant devApplicant = new Applicant();

    /**
     * Constructor for index controller.
     * @param apiService Service to contact backend.
     * @param clientRepository Repo to access client template.
     */
    @Autowired
    public IndexController(APIService apiService, ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.apiService = apiService;


        /*log.trace("A TRACE Message");
        log.debug("A DEBUG Message");
        log.info("An INFO Message");
        log.warn("A WARN Message");
        log.error("An ERROR Message");*/

        devApplicant.setEmail("veraalindss@gmail.com");
        devApplicant.setName("Vera");
        devApplicant.setPnr("000978");
        devApplicant.setSurname("Lindström");
        devApplicant.setUsername("veraalindss");

        ArrayList<ApplicationCompetence> competenceList2 = new ArrayList<>();
        competenceList2.add(new ApplicationCompetence("kassa", 5));
        competenceList2.add(new ApplicationCompetence("kök", 0.4));
        ApplicationDTO app2 = new ApplicationDTO("UNHANDLED", 16, 1, competenceList2, Date.valueOf("2023-04-05"), Date.valueOf("2026-02-05"), devApplicant);

        ArrayList<ApplicationCompetence> competenceList4 = new ArrayList<>();
        competenceList4.add(new ApplicationCompetence("kassa", 6));
        competenceList4.add(new ApplicationCompetence("kök", 20));
        competenceList4.add(new ApplicationCompetence("utkörning", 1));
        ApplicationDTO app4 = new ApplicationDTO("ACCEPTED", 10, 1, competenceList4, Date.valueOf("2022-04-05"), Date.valueOf("2023-02-05"), devApplicant);

        applicationList.add(app2);
        applicationList.add(app4);
    }

    /**
     * To get index page if user is logged in, else redirect to login page
     * @param userToken Cookie string value to authorize user.
     * @param model The current state of the model.
     * @return Index page or login page.
     */
    @GetMapping("/")
    public String home(@CookieValue(name = "token", required = false) String userToken, Model model) throws URISyntaxException {
        if(!apiService.clientExists(userToken)){
            log.info("Client not authenticated in \"home\" - redirected to log in");
            return "redirect:/login";
        }

        List<Competence> competenceList  = apiService.getPredefinedCompetences(userToken);
        List<Competence> modifiedCompetenceList = new ArrayList<>();
        List<String> competenceForFilter = new ArrayList<>();
        competenceForFilter.add("lotteries");

        modifiedCompetenceList.add(new Competence("alla"));
        for(Competence c : competenceList){
            modifiedCompetenceList.add(c);
        }

        model.addAttribute("competences", modifiedCompetenceList);


        // TODO: get list of applicants from backend
        // TODO: extract each applicant application and put in list together with applicant name
        //take list and send to model

        List<ApplicationDTO> listOfApplications = apiService.findApplicationsByCompetence(userToken, competenceForFilter, 1);
        for(ApplicationDTO a : listOfApplications){
            System.out.println("Application");
            System.out.println(a);
            System.out.println(a.getState());
        }

        model.addAttribute("applicationList", applicationList);
        model.addAttribute("applicant", devApplicant);
        model.addAttribute("length", applicationList.size());

        // FILTER OBJECT TO FILL AND SEND TO BACKEND //
        FilterRequest filterRequest = new FilterRequest();
        model.addAttribute("filter", filterRequest);

        return "index";
    }

    /**
     * Get login page for unauthenticated user. If user is already authenticated get to index page.
     * @param userToken Cookie string value to authorize user.
     * @return Login page or index page.
     */
    @GetMapping("/login")
    public String login(@CookieValue(value = "token", required = false) String userToken) throws Exception {
        if(Objects.nonNull(userToken) && apiService.clientExists(userToken)){
            return "redirect:/";
        }
        return "public/loginPage";
    }

    /**
     * POST method for login request handling the input and redirection from the login page.
     * @param model The current state of the model
     * @param userToken The cookie string value for authorization
     * @param username The users input of username
     * @param password The users input of password
     * @param response The HTTP response to add and access cookies
     * @return Login page if user input is invalid and Home page if user is authenticated
     * @throws Exception for an instance of HttpClientErrorException when invalid user input
     */
    @PostMapping("/login")
    public String login(Model model, @CookieValue(value = "token", required = false) String userToken, @RequestParam String username ,@RequestParam String password, HttpServletResponse response) throws HttpClientErrorException {
        try{
            if(Objects.nonNull(userToken) && apiService.clientExists(userToken)){
                return "redirect:/";
            }

            String token = apiService.usernamePasswordAuthentication(username,password);
            Cookie cookie = new Cookie("token", token);
            response.addCookie(cookie);
            return "redirect:/";
        } catch (HttpClientErrorException e) {
            String message = "Invalid username/password";
            model.addAttribute("message", message);
            log.error("Message: " + message + " Ex: " + e);
            return "public/loginPage";
        }
    }

    /**
     * To log out and be sent back to login page. UserToken is deleted from Client and Cookies.
     * @param userToken Cookie string value to authorize user.
     * @param response Http response to access and modify cookies.
     * @return The login page.
     */
    @GetMapping("/logout")
    public String logout(@CookieValue(value = "token", required = false) String userToken, HttpServletResponse response) {
        clientRepository.delete(userToken);
        Cookie cookie = new Cookie("token", null);
        response.addCookie(cookie);
        return "public/loginPage";
    }

}

