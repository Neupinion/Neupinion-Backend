package com.neupinion.neupinion.auth.ui;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@RequestScope
@Component
public class AuthContext {

    private Long memberId;

    public void setAuthenticatedMember(final Long memberId) {
        this.memberId = memberId;
    }
}
