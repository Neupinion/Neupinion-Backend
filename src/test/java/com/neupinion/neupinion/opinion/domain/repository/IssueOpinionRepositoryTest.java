package com.neupinion.neupinion.opinion.domain.repository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.repository.dto.IssueOpinionMapping;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@SuppressWarnings("NonAsciiCharacters")
public class IssueOpinionRepositoryTest extends JpaRepositoryTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    @Autowired
    private ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @Autowired
    private FollowUpIssueOpinionRepository followUpIssueOpinionRepository;

    @Test
    void 동일한_재가공_이슈에_속한_의견들을_조회한다() {
        // given
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지", Category.ECONOMY));
        final FollowUpIssue followUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("제목1", "image", Category.ECONOMY, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssue.getId()));
        final FollowUpIssue followUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("제목2", "image", Category.ECONOMY, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssue.getId()));

        final ReprocessedIssueOpinion reprocessedIssueOpinion1 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(1L, reprocessedIssue.getId(), true, 1L, "댓글1"));
        final ReprocessedIssueOpinion reprocessedIssueOpinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(1L, reprocessedIssue.getId(), true, 2L, "댓글1"));
        final ReprocessedIssueOpinion reprocessedIssueOpinion3 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(2L, reprocessedIssue.getId(), true, 2L, "댓글1"));

        final FollowUpIssueOpinion followUpIssueOpinion1 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(1L, followUpIssue1.getId(), true, 1L, "댓글1"));
        final FollowUpIssueOpinion followUpIssueOpinion2 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(1L, followUpIssue1.getId(), true, 2L, "댓글1"));
        final FollowUpIssueOpinion followUpIssueOpinion3 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(2L, followUpIssue2.getId(), true, 1L, "댓글1"));
        final FollowUpIssueOpinion followUpIssueOpinion4 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(2L, followUpIssue2.getId(), true, 2L, "댓글1"));

        // when
        final var reprocessedIssueOpinions = reprocessedIssueRepository.findAllCommentsOrderByCreatedAtDesc(
            reprocessedIssue.getId(), List.of(followUpIssue1.getId(), followUpIssue2.getId()), List.of(true, false),
            PageRequest.of(0, 5));
        final var nextReprocessedIssueOpinions = reprocessedIssueRepository.findAllCommentsOrderByCreatedAtDesc(
            reprocessedIssue.getId(), List.of(followUpIssue1.getId(), followUpIssue2.getId()), List.of(true, false),
            PageRequest.of(1, 5));

        // then
        assertAll(
            () -> assertThat(reprocessedIssueOpinions).hasSize(5),
            () -> assertThat(reprocessedIssueOpinions.stream()
                                 .map(IssueOpinionMapping::id)
                                 .toList()).containsExactly(followUpIssueOpinion4.getId(),
                                                            followUpIssueOpinion3.getId(),
                                                            followUpIssueOpinion2.getId(),
                                                            followUpIssueOpinion1.getId(),
                                                            reprocessedIssueOpinion3.getId()),
            () -> assertThat(reprocessedIssueOpinions.stream()
                                 .map(IssueOpinionMapping::issueType)
                                 .toList()).containsExactly("FOLLOW_UP", "FOLLOW_UP", "FOLLOW_UP", "FOLLOW_UP",
                                                            "REPROCESSED"),
            () -> assertThat(nextReprocessedIssueOpinions).hasSize(2),
            () -> assertThat(nextReprocessedIssueOpinions.stream()
                                 .map(IssueOpinionMapping::id)
                                 .toList()).containsExactly(reprocessedIssueOpinion2.getId(),
                                                            reprocessedIssueOpinion1.getId()),
            () -> assertThat(nextReprocessedIssueOpinions.stream()
                                 .map(IssueOpinionMapping::issueType)
                                 .toList()).containsExactly("REPROCESSED", "REPROCESSED")
        );
    }
}
