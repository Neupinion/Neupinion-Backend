package com.neupinion.neupinion.opinion.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionRepository;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class FollowUpIssueOpinionRepositoryTest extends JpaRepositoryTest {

    @Autowired
    private FollowUpIssueOpinionRepository followUpIssueOpinionRepository;

    @Test
    void 멤버의_id로_Opinion을_조회한다() {
        // given
        final long targetMemberId = 1L;
        final long otherMemberId = 2L;
        final FollowUpIssueOpinion followUpIssueOpinion1 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(1L, 1L, true, targetMemberId, "내용"));
        final FollowUpIssueOpinion followUpIssueOpinion2 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(2L, 2L, true, targetMemberId, "내용"));
        final FollowUpIssueOpinion followUpIssueOpinion3 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(3L, 3L, true, targetMemberId, "내용"));
        final FollowUpIssueOpinion followUpIssueOpinion4 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(4L, 4L, true, targetMemberId, "내용"));
        followUpIssueOpinionRepository.save(FollowUpIssueOpinion.forSave(1L, 1L, true, otherMemberId, "내용"));
        followUpIssueOpinionRepository.save(FollowUpIssueOpinion.forSave(1L, 2L, true, otherMemberId, "내용"));
        followUpIssueOpinionRepository.save(FollowUpIssueOpinion.forSave(1L, 3L, true, otherMemberId, "내용"));

        saveAndClearEntityManager();

        // when
        final Set<FollowUpIssueOpinion> followUpIssueOpinions = followUpIssueOpinionRepository.findByMemberId(
            targetMemberId);

        // then
        assertAll(
            () -> assertThat(followUpIssueOpinions).hasSize(4),
            () -> assertThat(followUpIssueOpinions).allMatch(opinion -> opinion.getMemberId().equals(targetMemberId)),
            () -> assertThat(followUpIssueOpinions).containsExactlyInAnyOrder(followUpIssueOpinion1,
                                                                              followUpIssueOpinion2,
                                                                              followUpIssueOpinion3,
                                                                              followUpIssueOpinion4)
        );
    }

    @Test
    void 이미_동일한_문단에_등록된_의견인지_확인한다() {
        // given
        final long memberId = 1L;
        final long paragraphId = 1L;
        final long issueId = 1L;

        followUpIssueOpinionRepository.save(FollowUpIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용"));

        saveAndClearEntityManager();

        // when
        final boolean isExisted = followUpIssueOpinionRepository.existsByMemberIdAndParagraphId(memberId, paragraphId);

        // then
        assertThat(isExisted).isTrue();
    }
}
