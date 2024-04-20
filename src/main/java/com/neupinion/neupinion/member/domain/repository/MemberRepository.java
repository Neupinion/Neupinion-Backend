package com.neupinion.neupinion.member.domain.repository;

import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.member.exception.MemberException.MemberNotFoundException;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    default Member getMemberById(final Long id) {
        return findById(id)
            .orElseThrow(() -> new MemberNotFoundException(Map.of("id", id.toString())));
    }
}
