package com.neupinion.neupinion.issue.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import com.neupinion.neupinion.issue.application.dto.FollowUpIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueOfVotedReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueResponse;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.RelatableStand;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTrustVote;
import com.neupinion.neupinion.issue.domain.event.FollowUpIssueViewedEvent;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTrustVoteRepository;
import com.neupinion.neupinion.issue.exception.ReprocessedIssueException.ReprocessedIssueNotFoundException;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionRepository;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

@SuppressWarnings("NonAsciiCharacters")
class FollowUpIssueServiceTest extends JpaRepositoryTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    @Autowired
    private FollowUpIssueOpinionRepository followUpIssueOpinionRepository;

    @Autowired
    private ReprocessedIssueTrustVoteRepository reprocessedIssueTrustVoteRepository;

    @MockBean
    private ApplicationEventPublisher publisher;

    private FollowUpIssueService followUpIssueService;

    @BeforeEach
    void setUp() {
        followUpIssueService = new FollowUpIssueService(reprocessedIssueRepository, followUpIssueRepository,
                                                        followUpIssueOpinionRepository, publisher);
    }

    @Test
    void 후속_이슈를_생성한다() {
        // given
        final ReprocessedIssue savedReprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지", Category.ECONOMY));
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
            ReprocessedIssue.forSave("제목1", "image", "이미지", Category.ECONOMY));

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

        followUpIssueOpinionRepository.save(FollowUpIssueOpinion.forSave(1L, savedFollowUpIssue1.getId(), true, memberId, "내용"));
        followUpIssueOpinionRepository.save(FollowUpIssueOpinion.forSave(1L, savedFollowUpIssue2.getId(), true, memberId, "내용"));

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

    @Test
    void 후속_이슈_아이디로_후속_이슈를_조회하고_조회수를_1_증가시킨다() {
        // given
        final Category category = Category.ECONOMY;
        final ReprocessedIssue savedReprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지", Category.ECONOMY));
        final FollowUpIssue followUpIssue = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목1", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.INTERVIEW, savedReprocessedIssue.getId()));
        final Long memberId = 1L;

        // when
        final FollowUpIssueResponse response = followUpIssueService.findById(followUpIssue.getId(), memberId);

        // then
        verify(publisher, only()).publishEvent(new FollowUpIssueViewedEvent(followUpIssue.getId(), memberId));
    }

    @Test
    void 유저가_조회하지_않은_후속_이슈를_최신순으로_조회한다() {
        // given
        final Long memberId = 1L;
        final Category category = Category.ECONOMY;

        final ReprocessedIssue savedReprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지", Category.ECONOMY));

        final FollowUpIssue savedFollowUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목1", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.INTERVIEW, savedReprocessedIssue.getId(),
                                  Clock.fixed(Instant.parse("2024-02-06T10:00:00Z"),
                                              Clock.systemDefaultZone().getZone())));
        final FollowUpIssue savedFollowUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목2", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.INTERVIEW, savedReprocessedIssue.getId(),
                                  Clock.fixed(Instant.parse("2024-02-07T10:00:00Z"),
                                              Clock.systemDefaultZone().getZone())));
        final FollowUpIssue savedFollowUpIssue3 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목3", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.INTERVIEW, savedReprocessedIssue.getId(),
                                  Clock.fixed(Instant.parse("2024-02-08T10:00:00Z"),
                                              Clock.systemDefaultZone().getZone())));
        final FollowUpIssue savedFollowUpIssue4 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목4", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.INTERVIEW, savedReprocessedIssue.getId(),
                                  Clock.fixed(Instant.parse("2024-02-09T10:00:00Z"),
                                              Clock.systemDefaultZone().getZone())));

        reprocessedIssueTrustVoteRepository.save(ReprocessedIssueTrustVote.forSave(savedReprocessedIssue.getId(), memberId, new RelatableStand(1L, true, 2L, false)));

        // when
        final List<FollowUpIssueOfVotedReprocessedIssueResponse> responses = followUpIssueService.findFollowUpIssuesOfVotedReprocessedIssue(
            memberId);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(3),
            () -> assertThat(responses).usingRecursiveComparison()
                .comparingOnlyFields("id")
                .isEqualTo(
                    List.of(savedFollowUpIssue4.getId(), savedFollowUpIssue3.getId(), savedFollowUpIssue2.getId()))
        );
    }
}
