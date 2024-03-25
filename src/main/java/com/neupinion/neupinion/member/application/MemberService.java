package com.neupinion.neupinion.member.application;

import com.neupinion.neupinion.member.domain.repository.MemberRepository;
import com.neupinion.neupinion.member.exception.MemberException.MemberNotFoundException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public void validateByIdThrowIfNotExist(final Long memberId) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(
                Map.of("memberId", memberId.toString())
            ));
    }
}
