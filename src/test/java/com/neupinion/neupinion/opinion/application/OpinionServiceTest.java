package com.neupinion.neupinion.opinion.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.neupinion.neupinion.issue.domain.FollowUpIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.issue.exception.ParagraphException.ParagraphForOtherIssueException;
import com.neupinion.neupinion.opinion.application.dto.FollowUpIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import com.neupinion.neupinion.opinion.exception.OpinionException.AlreadyExistedOpinionException;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class OpinionServiceTest extends JpaRepositoryTest {

    @Autowired
    private FollowUpIssueOpinionRepository followUpIssueOpinionRepository;

    @Autowired
    private FollowUpIssueParagraphRepository followUpIssueParagraphRepository;

    @Autowired
    private ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @Autowired
    private ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;

    private OpinionService opinionService;

    @BeforeEach
    void setUp() {
        opinionService = new OpinionService(followUpIssueOpinionRepository, followUpIssueParagraphRepository,
                                            reprocessedIssueOpinionRepository, reprocessedIssueParagraphRepository);
    }

    @Nested
    class 후속_이슈 {

        @Test
        void 후속_이슈의_의견을_생성한다() {
            // given
            final Long paragraphId = followUpIssueParagraphRepository.save(
                FollowUpIssueParagraph.forSave("내용", false, 1L)).getId();
            final FollowUpIssueOpinionCreateRequest request = FollowUpIssueOpinionCreateRequest.of(1L, paragraphId,
                                                                                                   "내용");
            final Long memberId = 1L;

            // when
            final Long opinionId = opinionService.createFollowUpIssueOpinion(memberId, request);

            // then
            assertThat(opinionId).isNotNull();
        }

        @Test
        void 멤버가_이미_해당_단락의_의견을_단_상태인_경우_예외가_발생한다() {
            // given
            final long followUpIssueId = 1L;
            final Long paragraphId = followUpIssueParagraphRepository.save(
                FollowUpIssueParagraph.forSave("내용", true, followUpIssueId)).getId();
            final FollowUpIssueOpinionCreateRequest request = FollowUpIssueOpinionCreateRequest.of(paragraphId,
                                                                                                   followUpIssueId,
                                                                                                   "내용");
            final long memberId = 1L;
            followUpIssueOpinionRepository.save(
                FollowUpIssueOpinion.forSave(paragraphId, followUpIssueId, memberId, "내용"));

            saveAndClearEntityManager();

            // when
            // then
            assertThatThrownBy(() -> opinionService.createFollowUpIssueOpinion(memberId, request))
                .isInstanceOf(AlreadyExistedOpinionException.class);
        }

        @Test
        void 추가할_단락이_입력받은_이슈와_다른_이슈의_단락이면_예외가_발생한다() {
            // given
            final long followUpIssueId = 1L;
            final Long paragraphId = followUpIssueParagraphRepository.save(
                FollowUpIssueParagraph.forSave("내용", true, followUpIssueId)).getId();
            final long memberId = 1L;
            final long otherFollowUpIssueId = Long.MAX_VALUE;
            final FollowUpIssueOpinionCreateRequest request = FollowUpIssueOpinionCreateRequest.of(paragraphId,
                                                                                                   otherFollowUpIssueId,
                                                                                                   "내용");

            saveAndClearEntityManager();

            // when
            // then
            assertThatThrownBy(() -> opinionService.createFollowUpIssueOpinion(memberId, request))
                .isInstanceOf(ParagraphForOtherIssueException.class);
        }
    }

    @Nested
    class 재가공_이슈 {

        @Test
        void 재가공_이슈의_의견을_생성한다() {
            // given
            final Long paragraphId = reprocessedIssueParagraphRepository.save(
                ReprocessedIssueParagraph.forSave("내용", false, 1L)).getId();
            final Long memberId = 1L;
            final String content = "내용";
            final Long reprocessedIssueId = 1L;

            // when
            final ReprocessedIssueOpinionCreateRequest request = ReprocessedIssueOpinionCreateRequest.of(
                paragraphId, reprocessedIssueId, content);
            final Long opinionId = opinionService.createReprocessedIssueOpinion(memberId, request);

            // then
            assertThat(opinionId).isNotNull();
        }

        @Test
        void 멤버가_이미_해당_단락의_의견을_단_상태인_경우_예외가_발생한다() {
            // given
            final long reprocessedIssueId = 1L;
            final Long paragraphId = reprocessedIssueParagraphRepository.save(
                ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId)).getId();
            final long memberId = 1L;
            final ReprocessedIssueOpinionCreateRequest request = ReprocessedIssueOpinionCreateRequest.of(
                paragraphId, reprocessedIssueId, "내용");
            reprocessedIssueOpinionRepository.save(
                ReprocessedIssueOpinion.forSave(paragraphId, reprocessedIssueId, memberId, "내용"));

            saveAndClearEntityManager();

            // when
            // then
            assertThatThrownBy(() -> opinionService.createReprocessedIssueOpinion(memberId, request))
                .isInstanceOf(AlreadyExistedOpinionException.class);
        }

        @Test
        void 추가할_단락이_입력받은_이슈와_다른_이슈의_단락이면_예외가_발생한다() {
            // given
            final long reprocessedIssueId = 1L;
            final Long paragraphId = reprocessedIssueParagraphRepository.save(
                ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId)).getId();
            final long memberId = 1L;
            final long otherReprocessedIssueId = Long.MAX_VALUE;
            final ReprocessedIssueOpinionCreateRequest request = ReprocessedIssueOpinionCreateRequest.of(
                paragraphId, otherReprocessedIssueId, "내용");

            saveAndClearEntityManager();

            // when
            // then
            assertThatThrownBy(() -> opinionService.createReprocessedIssueOpinion(memberId, request))
                .isInstanceOf(ParagraphForOtherIssueException.class);
        }
    }
}
