package com.iv1201.recruiterwebapp.recruiterwebapp.service;


import com.iv1201.recruiterwebapp.recruiterwebapp.DTO.ApplicationDTO;
import com.iv1201.recruiterwebapp.recruiterwebapp.model.*;
import com.iv1201.recruiterwebapp.recruiterwebapp.model.*;
import com.iv1201.recruiterwebapp.recruiterwebapp.repository.ClientRepository;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * The class handles the API calls to and from the server.
 * The service provides the requested data, as well as sends
 * given data to the server.
 */
@Service
public class APIService {
    private RestTemplate loginTemplate;
    private final ClientRepository clientRepository;

    @Autowired
    public APIService(RestTemplateBuilder restTemplateBuilder, ClientRepository clientRepository) {
        loginTemplate = restTemplateBuilder
                .build();
        this.clientRepository = clientRepository;
    }

    /**
     * Service method to authenticate against the back-end API
     * On success : Returns a session id that maps to the jwt token that is generated by the back-end server
     * On failure : Throws HttpClientErrorException that is handled in Exception handling controller
     * */
    public String usernamePasswordAuthentication(String username, String password){
        String url = "http://localhost:8080/api/users/recruiter/authenticate";
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.username = username;
        loginRequest.password = password;
        ResponseEntity<Map>response;

        response = loginTemplate.postForEntity(url,loginRequest,Map.class);

        if(response.getStatusCodeValue() != 200)
        {
            throw new HttpClientErrorException(response.getStatusCode());
        }

        JwtToken jwtToken = JwtToken.builder()
                .token((String) response.getBody().get("token"))
                .type((String) response.getBody().get("type"))
                .build();

        String key = UUID.randomUUID().toString();
        clientRepository.save(key,new ClientTemplate(jwtToken));
        return key;
    }


    public void filterRequest(FilterRequest filterRequest){

        String url = "http://localhost:8080/api/users/recruiter/filter";
        ResponseEntity<Map>response = loginTemplate.postForEntity(url, filterRequest,Map.class);
        if(response.getStatusCodeValue() != 200)
        {
            throw new HttpClientErrorException(response.getStatusCode());
        }
    }

    /**
     * Service method to get predefined competences from back-end (database)
     * On success : Returns the applicant found
     * If user doesn't exist : Throws HttpClientErrorException that is handled in Exception handling controller
     * On failure : TODO throw exception if getForObject fails? check assert
     * Get list of predefined competences from database.
     * @param clientId user token for authorization.
     * @return list of competences.
     */
    public List<Competence> getPredefinedCompetences(String clientId){
        String url = "http://localhost:8080/api/competences/";
        if(!clientRepository.exists(clientId)){
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        } else {
            ClientTemplate clientTemplate = clientRepository.find(clientId);
            Competence[] competences = clientTemplate.getTemplate().getForObject(url, Competence[].class);
            assert competences != null;
            return Arrays.asList(competences);
        }
    }

    /**
     * Fetch Page of applications filtered by competence
     * Return paginated result of applications that have a competence present in @param competences
     * Each page contain 100 elements
     * @param page what page to fetch
     * @param competences Element to filter the applications by
     * @param clientId The user identifier
     * @return the list of applications
     * */
    public List<ApplicationDTO> findApplicationsByCompetence(String clientId, List<String> competences, int page) throws URISyntaxException {
        // TODO : understand this???
        StringBuilder cmp = new StringBuilder();
        for(int i = 0; i < competences.size(); i++){
            cmp.append(competences.get(i));
            if(i < competences.size() -1){
                cmp.append(",");
            }
        }

        String url = String.format("http://localhost:8080/api/users/recruiter/applications/competences/%s/page?size=100&page=%s", cmp.toString(), page);
        URI uri = new URI(url);

        if(Objects.isNull(clientId)){
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ResponseEntity<Map> response = clientRepository.find(clientId).getTemplate().getForEntity(uri, Map.class);

        if(response.getStatusCodeValue() != 200)
        {
            throw new HttpClientErrorException(response.getStatusCode());
        }

        List<ApplicationDTO> applications = (List<ApplicationDTO>) response.getBody().get("content");

        return applications;
    }

    /**
     * Find the application based on the application identifier.
     * @param clientId The user identifier
     * @param id The application identifier
     * @return the application
     * @throws URISyntaxException
     */
    public ApplicationDTO findApplicationById(String clientId, long id) throws URISyntaxException {
        String url = String.format("http://localhost:8080/api/users/recruiter/applications/%s/",id);
        URI uri = new URI(url);

        if(Objects.isNull(clientId)){
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ResponseEntity<ApplicationDTO> response = clientRepository.find(clientId).getTemplate().getForEntity(uri, ApplicationDTO.class);

        if(response.getStatusCodeValue() != 200)
        {
            throw new HttpClientErrorException(response.getStatusCode());
        }

        return response.getBody();
    }

    /**
     * Updating the state of the application and based on version check conflicts for two users updating
     * the same application.
     * @param clientId The user identifier
     * @param applicationId The application id
     * @param version The current version if another user/recruiter is viewing same app
     * @param newState The new state for the application
     * @return the application
     * @throws URISyntaxException
     */
    public ApplicationDTO updateApplicationState(String clientId, long applicationId, long version, String newState) throws URISyntaxException {
        String url = String.format("http://localhost:8080/api/users/recruiter/applications/%s/state",applicationId);
        URI uri = new URI(url);

        if(Objects.isNull(clientId)){
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // TODO : error handling
        Map<String, Object> body = new HashMap<>();

        body.put("newState", newState);
        body.put("version", version);

        HttpEntity<?> httpEntity = new HttpEntity<Object>(body);
        ResponseEntity<ApplicationDTO> response = clientRepository.find(clientId).getTemplate().exchange(uri, HttpMethod.PATCH,httpEntity,ApplicationDTO.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            System.out.println("APIService: Error in status when updating state");
            throw new HttpClientErrorException(response.getStatusCode());
        }
        return response.getBody();
    }

    /**
     * Service method to find client by userToken
     * On success : True, if user was found. False, if user was not found.
     * On failure : False, if user was null.
     * */
    public boolean clientExists(String clientId){
        if(Objects.isNull(clientId)){
            return false;
        }
        return clientRepository.exists(clientId);
    }
}
