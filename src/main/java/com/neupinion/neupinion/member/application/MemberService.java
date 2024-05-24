package com.neupinion.neupinion.member.application;

import com.neupinion.neupinion.auth.application.OAuthType;
import com.neupinion.neupinion.member.domain.DefaultNicknamePolicy;
import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.member.domain.repository.MemberRepository;
import com.neupinion.neupinion.member.exception.MemberException.MemberNotFoundException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private static final String DEFAULT_NICKNAME_URL = "https://neupinion.com/image/default-profile.png";

    private final MemberRepository memberRepository;
    private final DefaultNicknamePolicy defaultNicknamePolicy;

    public void validateByIdThrowIfNotExist(final Long memberId) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(
                Map.of("memberId", memberId.toString())
            ));
    }

    public Optional<Member> findByAuthKeyAndAuthType(final String authKey, final OAuthType oAuthType) {
        return memberRepository.findByAuthKeyAndAuthType(authKey, oAuthType);
    }

    @Transactional
    public Member registerMember(final String memberInfo, final OAuthType oAuthType) {
        final String nickname = defaultNicknamePolicy.generate();

        return memberRepository.save(Member.forSave(nickname, DEFAULT_NICKNAME_URL, oAuthType, memberInfo));
    }
}
