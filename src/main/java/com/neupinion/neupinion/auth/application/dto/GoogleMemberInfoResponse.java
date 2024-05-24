package com.neupinion.neupinion.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GoogleMemberInfoResponse {

    private String email;

    @JsonProperty("verified_email")
    private boolean verifiedEmail;

}
