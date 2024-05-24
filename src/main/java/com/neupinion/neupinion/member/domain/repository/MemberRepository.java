package com.neupinion.neupinion.member.domain.repository;

import com.neupinion.neupinion.auth.application.OAuthType;
import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.member.exception.MemberException.MemberNotFoundException;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    default Member getMemberById(final Long id) {
        return findById(id)
            .orElseThrow(() -> new MemberNotFoundException(Map.of("id", id.toString())));
    }

    default Member getMemberByAuthKeyAndAuthType(final String authKey, final OAuthType oAuthType) {
        return findByAuthKeyAndAuthType(authKey, oAuthType)
            .orElseThrow(
                () -> new MemberNotFoundException(Map.of("authKey", authKey, "oAuthType", oAuthType.name()))
            );
    }

    Optional<Member> findByAuthKeyAndAuthType(final String authKey, final OAuthType authType);
}
