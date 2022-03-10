package com.iv1201.recruiterwebapp.recruiterwebapp.model;

import lombok.*;

import java.util.List;

/**
 * This class represents the JWT token that is used to represent claims to be transferred
 * between two parties.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtToken {
    private String token;
    private String type = "Bearer";
}