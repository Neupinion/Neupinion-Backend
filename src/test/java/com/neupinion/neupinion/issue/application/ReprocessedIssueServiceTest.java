package com.neupinion.neupinion.issue.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.ShortReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.TrustVoteRequest;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTag;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTrustVote;
import com.neupinion.neupinion.issue.domain.VoteStatus;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueBookmarkRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTagRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTrustVoteRepository;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReprocessedIssueServiceTest extends JpaRepositoryTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @Autowired
    private ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;

    @Autowired
    private ReprocessedIssueTagRepository reprocessedIssueTagRepository;

    @Autowired
    private ReprocessedIssueBookmarkRepository reprocessedIssueBookmarkRepository;

    @Autowired
    private ReprocessedIssueTrustVoteRepository reprocessedIssueTrustVoteRepository;

    private ReprocessedIssueService reprocessedIssueService;

    @BeforeEach
    void setUp() {
        reprocessedIssueService = new ReprocessedIssueService(reprocessedIssueRepository,
                                                              reprocessedIssueParagraphRepository,
                                                              reprocessedIssueTagRepository,
                                                              reprocessedIssueBookmarkRepository,
                                                              reprocessedIssueTrustVoteRepository);
    }

    @Test
    void 특정_날짜의_재가공_이슈를_조회한다() {
        // given
        final Clock clock = Clock.fixed(Instant.parse("2024-02-04T10:00:00Z"), ZoneId.of("Asia/Seoul"));
        final Clock clock2 = Clock.fixed(Instant.parse("2024-02-05T10:00:00Z"), ZoneId.of("Asia/Seoul"));
        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", "originUrl", Category.ECONOMY, clock2));
        final ReprocessedIssue issue2 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목2", "image", "이미지 캡션", "originUrl", Category.ECONOMY, clock));
        final ReprocessedIssue issue3 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목3", "image", "이미지 캡션", "originUrl", Category.ECONOMY, clock));
        final ReprocessedIssue issue4 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목4", "image", "이미지 캡션", "originUrl", Category.ECONOMY, clock));
        final ReprocessedIssue issue5 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목5", "image", "이미지 캡션", "originUrl", Category.ECONOMY, clock));

        // when
        final List<ShortReprocessedIssueResponse> issues = reprocessedIssueService.findReprocessedIssues("20240204");

        // then
        // 5 4 3 2
        assertThat(issues.stream()
                       .map(ShortReprocessedIssueResponse::getId)
                       .toList())
            .containsExactlyInAnyOrder(issue5.getId(), issue4.getId(), issue3.getId(), issue2.getId());
    }

    @Test
    void 재가공_이슈의_내용을_조회한다() {
        // given
        final ReprocessedIssue issue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", "originUrl", Category.ECONOMY));
        final ReprocessedIssueParagraph paragraph1 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용1", true, issue.getId()));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용2", true, issue.getId()));
        final ReprocessedIssueParagraph paragraph3 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용3", true, issue.getId()));
        final ReprocessedIssueParagraph paragraph4 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용4", true, issue.getId()));
        final ReprocessedIssueParagraph paragraph5 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용5", true, issue.getId()));

        final ReprocessedIssueTag tag1 = reprocessedIssueTagRepository.save(
            ReprocessedIssueTag.forSave(issue.getId(), "태그1"));
        final ReprocessedIssueTag tag2 = reprocessedIssueTagRepository.save(
            ReprocessedIssueTag.forSave(issue.getId(), "태그2"));
        final ReprocessedIssueTag tag3 = reprocessedIssueTagRepository.save(
            ReprocessedIssueTag.forSave(issue.getId(), "태그3"));

        final ReprocessedIssueTrustVote trustVote = reprocessedIssueTrustVoteRepository.save(
            ReprocessedIssueTrustVote.forSave(issue.getId(), 1L, "HIGHLY_TRUSTED"));

        final Long memberId = 1L;

        // when
        final ReprocessedIssueResponse response = reprocessedIssueService.findReprocessedIssue(memberId, issue.getId());

        // then
        assertAll(
            () -> assertThat(response.getId()).isEqualTo(issue.getId()),
            () -> assertThat(response.getTitle()).isEqualTo(issue.getTitle()),
            () -> assertThat(response.getImageUrl()).isEqualTo(issue.getImageUrl()),
            () -> assertThat(response.getCaption()).isEqualTo(issue.getCaption()),
            () -> assertThat(response.getCategory()).isEqualTo(issue.getCategory().getValue()),
            () -> assertThat(response.getContent())
                .usingRecursiveComparison()
                .comparingOnlyFields("id")
                .isEqualTo(List.of(paragraph1, paragraph2, paragraph3, paragraph4, paragraph5)),
            () -> assertThat(response.getCreatedAt()).isEqualTo(issue.getCreatedAt()),
            () -> assertThat(response.getOriginUrl()).isEqualTo(issue.getOriginUrl()),
            () -> assertThat(response.getTrustVote()).isEqualTo(trustVote.getStatus().name()),
            () -> assertThat(response.getIsBookmarked()).isFalse(),
            () -> assertThat(response.getTags())
                .containsExactlyInAnyOrder(tag1.getTag(), tag2.getTag(), tag3.getTag())
        );
    }

    @Test
    void 재가공_이슈를_조회할_때_신뢰도_평가가_되어_있지_않으면_NOT_VOTED를_반환한다() {
        // given
        final ReprocessedIssue issue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", "originUrl", Category.ECONOMY));
        final long memberId = 1L;

        // when
        final ReprocessedIssueResponse response = reprocessedIssueService.findReprocessedIssue(memberId, issue.getId());

        // then
        assertThat(response.getTrustVote()).isEqualTo("NOT_VOTED");
    }

    @Test
    void 재가공_이슈에_처음_신뢰도_투표를_한다() {
        // given
        final ReprocessedIssue issue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", "originUrl", Category.ECONOMY));
        final long memberId = 1L;
        final TrustVoteRequest request = new TrustVoteRequest("HIGHLY_TRUSTED");
        saveAndClearEntityManager();

        // when
        reprocessedIssueService.vote(memberId, issue.getId(), request);

        // then
        final ReprocessedIssueTrustVote trustVote = reprocessedIssueTrustVoteRepository.findByReprocessedIssueIdAndMemberId(
            issue.getId(), memberId).get();

        assertThat(trustVote.getStatus()).isEqualTo(VoteStatus.from(request.getStatus()));
    }

    @Test
    void 재가공_이슈에_신뢰도_투표를_업데이트한다() {
        // given
        final ReprocessedIssue issue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", "originUrl", Category.ECONOMY));
        final long memberId = 1L;
        final TrustVoteRequest request = new TrustVoteRequest("HIGHLY_TRUSTED");
        reprocessedIssueTrustVoteRepository.save(
            ReprocessedIssueTrustVote.forSave(issue.getId(), memberId, VoteStatus.NOT_VOTED.name()));

        // when
        reprocessedIssueService.vote(memberId, issue.getId(), request);

        // then
        final ReprocessedIssueTrustVote trustVote = reprocessedIssueTrustVoteRepository.findByReprocessedIssueIdAndMemberId(
            issue.getId(), memberId).get();

        assertThat(trustVote.getStatus()).isEqualTo(VoteStatus.from(request.getStatus()));
    }
}
