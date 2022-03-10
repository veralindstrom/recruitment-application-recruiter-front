package com.iv1201.recruiterwebapp.recruiterwebapp.controller;


import com.iv1201.recruiterwebapp.recruiterwebapp.DTO.ApplicationDTO;
import com.iv1201.recruiterwebapp.recruiterwebapp.model.*;
import com.iv1201.recruiterwebapp.recruiterwebapp.repository.ClientRepository;
import com.iv1201.recruiterwebapp.recruiterwebapp.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This controller handles  application logic; state changes on applications and filtering.
 */
@Controller
public class ApplicationController {
    private final Applicant devApplicant = new Applicant();
    private final ArrayList<ApplicationDTO> applicationList = new ArrayList<>();
    private final APIService apiService;

    /**
     * Constructor for application controller.
     */
    @Autowired
    public ApplicationController(APIService apiService) {
        this.apiService = apiService;

        devApplicant.setEmail("veraalindss@gmail.com");
        devApplicant.setName("Vera");
        devApplicant.setPnr("000978");
        devApplicant.setSurname("Lindström");
        devApplicant.setUsername("veraalindss");

        // HARDCODED EXAMPLE FOR TESTING RECRUITER FUNCS //

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
     * When user/recruiter wants to filter through all applications and not only visible applications
     * on one page. The form is sent to backend to receive an already filtered list of applications.
     * @param userToken Cookie string value to authorize user.
     * @param model Current state of model.
     * @param filterRequest The elements to filter the application by from backend.
     * @return Filter applications page.
     * @throws ParseException
     */
    @PostMapping("/filter")
    public String filterApplications(@CookieValue(value = "token") String userToken, Model model, @ModelAttribute("filter") FilterRequest filterRequest) throws ParseException {
        if(!apiService.clientExists(userToken)){
            return "redirect:/login";
        }
        List<Competence> competenceList  = apiService.getPredefinedCompetences(userToken);
        List<Competence> modifiedCompetenceList = new ArrayList<>();

        modifiedCompetenceList.add(new Competence("alla"));
        for(Competence c : competenceList){
            modifiedCompetenceList.add(c);
        }

        model.addAttribute("competences", modifiedCompetenceList);

        // TODO: get list of applicants from backend
        //take list and send to model
        model.addAttribute(applicationList);
        model.addAttribute("length", applicationList.size());


        // FILTER OBJECT TO FILL AGAIN //
        FilterRequest filterRequestNew = new FilterRequest();
        model.addAttribute("filter", filterRequestNew);

        // NEW CODE FOR FILTER TO DISPLAY OLD FILTER CHOICE//
        if (!filterRequest.getCompetence().equals("alla")) {
            //System.out.println(filterRequest.getCompetence());
            model.addAttribute("oldCompetence", filterRequest.getCompetence());
        }
        if (!filterRequest.getName().isEmpty() || !filterRequest.getName().isBlank()) {
            //System.out.println(filterRequest.getName());
            model.addAttribute("oldName", filterRequest.getName());
        }
        if (!filterRequest.getDateFrom().isEmpty() || !filterRequest.getDateFrom().isBlank()) {
            //System.out.println(filterRequest.getDateFrom());
            model.addAttribute("oldFrom", filterRequest.getDateFrom());
        }
        if (!filterRequest.getDateTo().isEmpty() || !filterRequest.getDateTo().isBlank()) {
            //System.out.println(filterRequest.getDateTo());
            model.addAttribute("oldTo", filterRequest.getDateTo());
        }

        return "index";
    }

    /**
     * When an application is chosen from the list, it is redirected to a new page to visualize
     * the application alone through its id.
     * @param userToken Cookie string value to authorize user.
     * @param model Current state of model.
     * @param id Application identifier
     * @return Application Page.
     * @throws ParseException
     */
    @GetMapping("/application/{id}")
    public String application(@CookieValue(name = "token") String userToken, Model model, @PathVariable Integer id) throws ParseException, URISyntaxException {
        if(!apiService.clientExists(userToken)){
            return "redirect:/login";
        }

        ApplicationDTO app = apiService.findApplicationById(userToken, id);

        model.addAttribute("app", app);
        model.addAttribute("applicant", app.getApplicant());

        return "public/applicationPage";
    }

    /**
     * When the user/recruiter want to change the state of an application from unhandled,
     * the new chosen state is set for the application.
     * @param userToken Cookie string value to authorize user.
     * @param model Current state of model.
     * @param id Application identifier
     * @param newState The new state to set on the application.
     * @return Application page.
     * @throws ParseException
     */
    @PostMapping("/application/{id}/updateState")
    public String applicationStateChange(@CookieValue(name = "token") String userToken, Model model,@PathVariable Integer id, @RequestParam String newState, @RequestParam String username, @RequestParam String password) throws URISyntaxException {
        if(!apiService.clientExists(userToken)){
            return "redirect:/login";
        }
        ApplicationDTO app = apiService.findApplicationById(userToken, id);
        try{
            apiService.usernamePasswordAuthentication(username,password);

            System.out.println("Try send to backend...");
            app = apiService.updateApplicationState(userToken, id, app.getVersion(), newState);

            String message = "The state was successfully changed!";
            model.addAttribute("successMsg", message);
            model.addAttribute("app", app);
            model.addAttribute("applicant", app.getApplicant());

            return "public/applicationPage";
        } catch (HttpClientErrorException e) {
            String message = "Could not change state of application: Invalid username/password";
            model.addAttribute("errorMsg", message);
            model.addAttribute("app", app);
            model.addAttribute("applicant", app.getApplicant());
            return "public/applicationPage";
        }
    }

}

