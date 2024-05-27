package com.neupinion.neupinion.opinion.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.neupinion.neupinion.opinion.application.dto.OpinionLikeRequest;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinionLike;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionLikeRepository;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class OpinionLikeServiceTest extends JpaRepositoryTest {

    @Autowired
    private ReprocessedIssueOpinionLikeRepository reprocessedIssueOpinionLikeRepository;

    private OpinionLikeService opinionLikeService;

    @BeforeEach
    void setUp() {
        opinionLikeService = new OpinionLikeService(reprocessedIssueOpinionLikeRepository);
    }

    @Test
    void 좋아요가_이미_존재하면_존재하는_좋아요의_상태를_바꾼다() {
        // given
        final long memberId = 1L;
        final long opinionId = 1L;
        reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(memberId, opinionId)
        );

        // when
        opinionLikeService.updateLike(opinionId, new OpinionLikeRequest(false), memberId);

        // then
        final ReprocessedIssueOpinionLike opinionLike = reprocessedIssueOpinionLikeRepository.findByMemberIdAndReprocessedIssueOpinionId(
            memberId, opinionId
        ).orElseThrow();
        assertThat(opinionLike.getIsDeleted()).isTrue();
    }
}
