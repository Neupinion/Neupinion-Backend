package com.neupinion.neupinion.opinion.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinionLike;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@SuppressWarnings("NonAsciiCharacters")
class ReprocessedIssueOpinionRepositoryTest extends JpaRepositoryTest {

    @Autowired
    private ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @Autowired
    private ReprocessedIssueOpinionLikeRepository reprocessedIssueOpinionLikeRepository;

    @Test
    void 이미_동일한_문단에_등록된_의견인지_확인한다() {
        // given
        final long memberId = 1L;
        final long paragraphId = 1L;
        final long issueId = 1L;

        reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용"));

        saveAndClearEntityManager();

        // when
        final boolean isExisted = reprocessedIssueOpinionRepository.existsByMemberIdAndParagraphId(memberId,
                                                                                                   paragraphId);

        // then
        assertThat(isExisted).isTrue();
    }

    @Test
    void 좋아요가_가장_많은_의견_5개를_가져온다() {
        // given
        final long memberId = 1L;
        final long paragraphId = 1L;
        final long issueId = 1L;

        final ReprocessedIssueOpinion opinion1 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용1"));
        final ReprocessedIssueOpinion opinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용2"));
        final ReprocessedIssueOpinion opinion3 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용3"));
        final ReprocessedIssueOpinion opinion4 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용4"));
        final ReprocessedIssueOpinion opinion5 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용5"));
        reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용6"));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion1.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion1.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion1.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion1.getId()));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion2.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion2.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion2.getId()));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion3.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion3.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion3.getId()));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion4.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion4.getId()));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion5.getId()));

        saveAndClearEntityManager();

        // when
        final List<ReprocessedIssueOpinion> opinions = reprocessedIssueOpinionRepository.findTop5ByActiveLikes(
            PageRequest.of(0, 5), issueId);

        // then
        assertAll(
            () -> assertThat(opinions).hasSize(5),
            () -> assertThat(opinions).containsExactly(opinion1, opinion3, opinion2, opinion4, opinion5)
        );
    }

    @Test
    void 좋아요가_0개인_의견도_함께_조회한다() {
        // given
        final long memberId = 1L;
        final long paragraphId = 1L;
        final long issueId = 1L;

        final ReprocessedIssueOpinion opinion1 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용1"));
        final ReprocessedIssueOpinion opinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용2"));
        final ReprocessedIssueOpinion opinion3 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용3"));
        final ReprocessedIssueOpinion opinion4 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용4"));
        final ReprocessedIssueOpinion opinion5 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용5"));
        final ReprocessedIssueOpinion opinion6 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraphId, issueId, true, memberId, "내용6"));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion1.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion1.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion1.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion1.getId()));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion2.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion2.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion2.getId()));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion3.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion3.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion3.getId()));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion4.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion4.getId()));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion5.getId()));

        saveAndClearEntityManager();

        // when
        final List<ReprocessedIssueOpinion> opinions = reprocessedIssueOpinionRepository.findOpinionsOrderByLike(
            issueId, List.of(true), PageRequest.of(0, 6));

        // then
        assertAll(
            () -> assertThat(opinions).hasSize(6),
            () -> assertThat(opinions).containsExactly(opinion1, opinion3, opinion2, opinion4, opinion5, opinion6)
        );
    }
}
