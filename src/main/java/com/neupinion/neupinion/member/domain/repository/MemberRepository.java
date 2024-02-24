package com.neupinion.neupinion.member.domain.repository;

import com.neupinion.neupinion.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
