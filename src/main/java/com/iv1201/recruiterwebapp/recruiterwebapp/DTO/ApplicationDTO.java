package com.iv1201.recruiterwebapp.recruiterwebapp.DTO;

import com.iv1201.recruiterwebapp.recruiterwebapp.model.Applicant;
import com.iv1201.recruiterwebapp.recruiterwebapp.model.ApplicationCompetence;
import lombok.*;

import java.sql.Date;
import java.util.List;

/**
 * This class represents an application and applicant, in order to list an application with
 * the name of the corresponding applicant.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDTO {
    private String state;

    private long id;

    private long version;

    private List<ApplicationCompetence> competences;

    private Date dateFrom;

    private Date dateTo;

    private Applicant applicant;
}