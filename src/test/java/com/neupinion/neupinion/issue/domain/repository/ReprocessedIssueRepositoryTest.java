package com.neupinion.neupinion.issue.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.dto.ReprocessedIssueWithCommentCount;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinionLike;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinionLike;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionLikeRepository;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionLikeRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import com.neupinion.neupinion.opinion.domain.repository.dto.IssueOpinionMapping;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@SuppressWarnings("NonAsciiCharacters")
class ReprocessedIssueRepositoryTest extends JpaRepositoryTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @Autowired
    private ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    @Autowired
    private FollowUpIssueOpinionRepository followUpIssueOpinionRepository;

    @Autowired
    private ReprocessedIssueOpinionLikeRepository reprocessedIssueOpinionLikeRepository;

    @Autowired
    private FollowUpIssueOpinionLikeRepository followUpIssueOpinionLikeRepository;

    @Test
    void 재가공_이슈_정보와_댓글_개수를_조회한다() {
        // given
        final Clock clock = Clock.fixed(Instant.parse("2024-02-04T10:00:00Z"), ZoneId.of("Asia/Seoul"));

        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지", Category.ECONOMY, clock));
        final ReprocessedIssue issue2 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목2", "image", "이미지", Category.ECONOMY, clock));
        final ReprocessedIssue issue3 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목3", "image", "이미지", Category.ECONOMY, clock));
        System.out.println("time" + LocalDateTime.now(clock));

        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(1L, issue1.getId(), true, 1L, "댓글1"));
        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(2L, issue1.getId(), true, 2L, "댓글2"));
        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(3L, issue1.getId(), true, 3L, "댓글3"));
        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(1L, issue2.getId(), true, 1L, "댓글1"));
        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(2L, issue2.getId(), true, 2L, "댓글2"));
        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(3L, issue3.getId(), true, 1L, "댓글1"));

        saveAndClearEntityManager();

        // when
        final var response = reprocessedIssueRepository.findByCreatedAt(LocalDate.of(2024, 2, 4),
                                                                        PageRequest.of(0, 4));
        // then
        assertAll(
            () -> assertThat(response).hasSize(3),
            () -> assertThat(response.stream()
                                 .map(ReprocessedIssueWithCommentCount::getReprocessedIssue)
                                 .map(ReprocessedIssue::getId)
                                 .toList()).containsExactlyInAnyOrder(issue1.getId(), issue2.getId(), issue3.getId()),
            () -> assertThat(response.stream()
                                 .map(ReprocessedIssueWithCommentCount::getCommentCount)
                                 .toList()).containsExactlyInAnyOrder(3L, 2L, 1L)
        );
    }

    @Test
    void 재가공_이슈와_후속_이슈의_의견을_시간순으로_한번에_조회한다() {
        // given
        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지", Category.ECONOMY));

        final ReprocessedIssueOpinion rio1 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(1L, issue1.getId(), true, 1L, "댓글1"));
        final ReprocessedIssueOpinion rio2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(2L, issue1.getId(), true, 2L, "댓글2"));
        final ReprocessedIssueOpinion rio3 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(3L, issue1.getId(), true, 3L, "댓글3"));

        final FollowUpIssue followUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("제목2", "image", Category.ECONOMY, FollowUpIssueTag.TRIAL_RESULTS, issue1.getId()));
        final FollowUpIssue followUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("제목3", "image", Category.ECONOMY, FollowUpIssueTag.TRIAL_RESULTS, issue1.getId()));

        final FollowUpIssueOpinion fio1 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(1L, followUpIssue1.getId(), true, 1L, "댓글1"));
        final FollowUpIssueOpinion fio2 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(2L, followUpIssue1.getId(), true, 2L, "댓글2"));
        final FollowUpIssueOpinion fio3 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(3L, followUpIssue2.getId(), true, 1L, "댓글1"));
        final FollowUpIssueOpinion fio4 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(4L, followUpIssue2.getId(), true, 2L, "댓글2"));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(1L, rio1.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(1L, rio2.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(1L, rio3.getId()));

        followUpIssueOpinionLikeRepository.save(FollowUpIssueOpinionLike.forSave(1L, fio1.getId()));
        followUpIssueOpinionLikeRepository.save(FollowUpIssueOpinionLike.forSave(1L, fio2.getId()));

        saveAndClearEntityManager();

        // when
        final var response = reprocessedIssueRepository.findAllCommentsOrderByCreatedAtDesc(issue1.getId(),
                                                                                            List.of(followUpIssue1.getId(), followUpIssue2.getId()),
                                                                                            List.of(true),
                                                                                            PageRequest.of(0, 5));

        // then
        assertAll(
            () -> assertThat(response).hasSize(5),
            () -> assertThat(response.stream()
                                 .map(IssueOpinionMapping::id)
                                 .toList()).containsExactly(fio4.getId(), fio3.getId(), fio2.getId(), fio1.getId(), rio3.getId()),
            () -> assertThat(response.stream()
                                 .map(IssueOpinionMapping::likeCount)
                                 .toList()).containsExactly(0, 0, 1, 1, 1)
        );
    }

    @Test
    void 재가공_이슈와_후속_이슈의_의견을_좋아요_순으로_한번에_조회한다() {
        // given
        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지", Category.ECONOMY));

        final ReprocessedIssueOpinion rio1 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(1L, issue1.getId(), true, 1L, "댓글1"));
        final ReprocessedIssueOpinion rio2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(2L, issue1.getId(), true, 2L, "댓글2"));
        final ReprocessedIssueOpinion rio3 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(3L, issue1.getId(), true, 3L, "댓글3"));

        final FollowUpIssue followUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("제목2", "image", Category.ECONOMY, FollowUpIssueTag.TRIAL_RESULTS, issue1.getId()));
        final FollowUpIssue followUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("제목3", "image", Category.ECONOMY, FollowUpIssueTag.TRIAL_RESULTS, issue1.getId()));

        final FollowUpIssueOpinion fio1 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(1L, followUpIssue1.getId(), true, 1L, "댓글1"));
        final FollowUpIssueOpinion fio2 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(2L, followUpIssue1.getId(), true, 2L, "댓글2"));
        final FollowUpIssueOpinion fio3 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(3L, followUpIssue2.getId(), true, 1L, "댓글1"));
        final FollowUpIssueOpinion fio4 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(4L, followUpIssue2.getId(), true, 2L, "댓글2"));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(1L, rio1.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(2L, rio1.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(3L, rio1.getId()));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(1L, rio2.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(2L, rio2.getId()));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(1L, rio3.getId()));

        followUpIssueOpinionLikeRepository.save(FollowUpIssueOpinionLike.forSave(1L, fio1.getId()));
        followUpIssueOpinionLikeRepository.save(FollowUpIssueOpinionLike.forSave(2L, fio1.getId()));
        followUpIssueOpinionLikeRepository.save(FollowUpIssueOpinionLike.forSave(3L, fio1.getId()));

        followUpIssueOpinionLikeRepository.save(FollowUpIssueOpinionLike.forSave(1L, fio2.getId()));
        followUpIssueOpinionLikeRepository.save(FollowUpIssueOpinionLike.forSave(2L, fio2.getId()));

        saveAndClearEntityManager();

        // when
        final var response = reprocessedIssueRepository.findAllCommentsOrderByLikesAtDesc(issue1.getId(),
                                                                                            List.of(followUpIssue1.getId(), followUpIssue2.getId()),
                                                                                            List.of(true),
                                                                                            PageRequest.of(0, 7));

        // then
        assertAll(
            () -> assertThat(response).hasSize(7),
            () -> assertThat(response.stream()
                                 .map(IssueOpinionMapping::id)
                                 .toList()).containsExactly(fio1.getId(), rio1.getId(), fio2.getId(), rio2.getId(), rio3.getId(), fio4.getId(), fio3.getId()),
            () -> assertThat(response.stream()
                                 .map(IssueOpinionMapping::likeCount)
                                 .toList()).containsExactly(3, 3, 2, 2, 1, 0, 0)
        );
    }
}
