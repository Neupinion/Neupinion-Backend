package com.neupinion.neupinion.issue.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.exception.ReprocessedIssueException.ReprocessedIssueNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
class FollowUpIssueServiceTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    private FollowUpIssueService followUpIssueService;

    @BeforeEach
    void setUp() {
        followUpIssueService = new FollowUpIssueService(reprocessedIssueRepository, followUpIssueRepository);
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
}
