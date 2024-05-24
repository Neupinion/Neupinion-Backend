package com.neupinion.neupinion.auth.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenPair {

    private String accessToken;
    private String refreshToken;
}
