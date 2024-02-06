package com.neupinion.neupinion.issue.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.application.dto.FollowUpIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.Opinion;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.OpinionRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.exception.ReprocessedIssueException.ReprocessedIssueNotFoundException;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class FollowUpIssueServiceTest extends JpaRepositoryTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    private FollowUpIssueService followUpIssueService;

    @BeforeEach
    void setUp() {
        followUpIssueService = new FollowUpIssueService(reprocessedIssueRepository, followUpIssueRepository,
                                                        opinionRepository);
    }

    @Test
    void 후속_이슈를_생성한다() {
        // given
        final ReprocessedIssue savedReprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", Category.ECONOMY));
        final FollowUpIssueCreateRequest request = FollowUpIssueCreateRequest.of("후속 이슈 제목",
                                                                                 Category.ECONOMY.name(),
                                                                                 "https://neupinion.com/image.jpg",
                                                                                 FollowUpIssueTag.INTERVIEW.name(),
                                                                                 savedReprocessedIssue.getId());

        // when
        final Long followUpIssueId = followUpIssueService.createFollowUpIssue(request);
        final FollowUpIssue followUpIssue = followUpIssueRepository.findById(followUpIssueId).get();

        // then
        assertAll(
            () -> assertThat(followUpIssue.getReprocessedIssueId()).isEqualTo(request.getReprocessedIssueId()),
            () -> assertThat(followUpIssue.getTitle().getValue()).isEqualTo(request.getTitle()),
            () -> assertThat(followUpIssue.getCategory().name()).isEqualTo(request.getCategory()),
            () -> assertThat(followUpIssue.getImageUrl()).isEqualTo(request.getImageUrl()),
            () -> assertThat(followUpIssue.getTag().name()).isEqualTo(request.getTag())
        );
    }

    @Test
    void 후속_이슈의_재가공_이슈가_존재하지_않으면_예외가_발생한다() {
        // given
        final long notExistedReprocessedId = 0L;
        final FollowUpIssueCreateRequest request = FollowUpIssueCreateRequest.of("후속 이슈 제목",
                                                                                 Category.ECONOMY.name(),
                                                                                 "https://neupinion.com/image.jpg",
                                                                                 FollowUpIssueTag.INTERVIEW.name(),
                                                                                 notExistedReprocessedId);

        // when
        // then
        assertThatThrownBy(() -> followUpIssueService.createFollowUpIssue(request))
            .isInstanceOf(ReprocessedIssueNotFoundException.class);
    }

    @Test
    void 카테고리와_날짜_멤버_아이디로_후속_이슈를_조회한다() {
        // given
        final Category category = Category.ECONOMY;
        final Category otherCategory = Category.SOCIETY;
        final long memberId = 1L;

        final ReprocessedIssue savedReprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", category));

        final Clock clock = Clock.fixed(Instant.parse("2024-02-06T10:00:00Z"), Clock.systemDefaultZone().getZone());
        final FollowUpIssue savedFollowUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목1", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.INTERVIEW, savedReprocessedIssue.getId(), clock));
        final FollowUpIssue savedFollowUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목2", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.INTERVIEW, savedReprocessedIssue.getId(), clock));
        final FollowUpIssue savedFollowUpIssue3 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목3", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.INTERVIEW, savedReprocessedIssue.getId(), clock));
        final FollowUpIssue savedFollowUpIssue4 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목4", "https://neupinion.com/image.jpg", otherCategory,
                                  FollowUpIssueTag.INTERVIEW, savedReprocessedIssue.getId(), clock));

        opinionRepository.save(Opinion.forSave(savedFollowUpIssue1.getId(), memberId));
        opinionRepository.save(Opinion.forSave(savedFollowUpIssue2.getId(), memberId));

        // when
        final var result = followUpIssueService.findFollowUpIssueByCategoryAndDate("20240206", category.name(),
                                                                                   memberId);

        // then
        assertAll(
            () -> assertThat(result).hasSize(3),
            () -> assertThat(result.stream()
                                 .map(FollowUpIssueByCategoryResponse::getId)
                                 .toList()).containsExactlyInAnyOrder(savedFollowUpIssue1.getId(),
                                                                      savedFollowUpIssue2.getId(),
                                                                      savedFollowUpIssue3.getId())
        );
    }
}
