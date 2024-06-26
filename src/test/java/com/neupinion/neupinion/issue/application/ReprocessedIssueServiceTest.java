package com.neupinion.neupinion.issue.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.bookmark.domain.repository.ReprocessedIssueBookmarkRepository;
import com.neupinion.neupinion.issue.application.dto.RecentReprocessedIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueVoteResultResponse;
import com.neupinion.neupinion.issue.application.dto.ShortReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.TrustVoteRequest;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.IssueStand;
import com.neupinion.neupinion.issue.domain.RelatableStand;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTag;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTrustVote;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.IssueStandReferenceRepository;
import com.neupinion.neupinion.issue.domain.repository.IssueStandRepository;
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

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    @Autowired
    private IssueStandRepository issueStandRepository;

    @Autowired
    private IssueStandReferenceRepository issueStandReferenceRepository;

    private ReprocessedIssueService reprocessedIssueService;

    @BeforeEach
    void setUp() {
        reprocessedIssueService = new ReprocessedIssueService(reprocessedIssueRepository,
                                                              reprocessedIssueParagraphRepository,
                                                              issueStandReferenceRepository,
                                                              reprocessedIssueTagRepository,
                                                              reprocessedIssueBookmarkRepository,
                                                              reprocessedIssueTrustVoteRepository,
                                                              issueStandRepository,
                                                              followUpIssueRepository);
    }

    @Test
    void 특정_날짜의_재가공_이슈를_조회한다() {
        // given
        final Clock clock = Clock.fixed(Instant.parse("2024-02-04T10:00:00Z"), ZoneId.of("Asia/Seoul"));
        final Clock clock2 = Clock.fixed(Instant.parse("2024-02-05T10:00:00Z"), ZoneId.of("Asia/Seoul"));
        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", Category.ECONOMY, clock2));
        final ReprocessedIssue issue2 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목2", "image", "이미지 캡션", Category.ECONOMY, clock));
        final ReprocessedIssue issue3 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목3", "image", "이미지 캡션", Category.ECONOMY, clock));
        final ReprocessedIssue issue4 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목4", "image", "이미지 캡션", Category.ECONOMY, clock));
        final ReprocessedIssue issue5 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목5", "image", "이미지 캡션", Category.ECONOMY, clock));

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
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", Category.ECONOMY));
        final List<IssueStand> stands = issueStandRepository.saveAll(List.of(
            IssueStand.forSave("찬성", issue.getId()),
            IssueStand.forSave("반대", issue.getId())
        ));
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

        reprocessedIssueTrustVoteRepository.save(ReprocessedIssueTrustVote.forSave(issue.getId(), 1L,
                                                                                   new RelatableStand(
                                                                                       stands.get(0).getId(), true,
                                                                                       stands.get(1).getId(), false)));

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
            () -> assertThat(response.getStands()).hasSize(2),
            () -> assertThat(response.getStands().get(0).getRelatable()).isTrue(),
            () -> assertThat(response.getStands().get(1).getRelatable()).isFalse(),
            () -> assertThat(response.getIsBookmarked()).isFalse(),
            () -> assertThat(response.getTags())
                .containsExactlyInAnyOrder(tag1.getTag(), tag2.getTag(), tag3.getTag())
        );
    }

    @Test
    void 재가공_이슈를_조회할_때_신뢰도_평가가_되어_있지_않으면_각_입장의_ID로_0을_반환한다() {
        // given
        final ReprocessedIssue issue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", Category.ECONOMY));
        issueStandRepository.saveAll(List.of(
            IssueStand.forSave("찬성", issue.getId()),
            IssueStand.forSave("반대", issue.getId())
        ));
        final long memberId = 1L;

        // when
        final ReprocessedIssueResponse response = reprocessedIssueService.findReprocessedIssue(memberId, issue.getId());

        // then
        assertThat(response.getIsVoted()).isFalse();
    }

    @Test
    void 재가공_이슈에_처음_신뢰도_투표를_한다() {
        // given
        final ReprocessedIssue issue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", Category.ECONOMY));
        final long memberId = 1L;
        final List<IssueStand> issueStands = issueStandRepository.saveAll(List.of(
            IssueStand.forSave("찬성", issue.getId()),
            IssueStand.forSave("반대", issue.getId())
        ));
        final TrustVoteRequest request = new TrustVoteRequest(issueStands.get(0).getId(), true,
                                                              issueStands.get(1).getId(), false);
        saveAndClearEntityManager();

        // when
        reprocessedIssueService.vote(memberId, issue.getId(), request);

        // then
        final ReprocessedIssueTrustVote trustVote = reprocessedIssueTrustVoteRepository.findByReprocessedIssueIdAndMemberId(
            issue.getId(), memberId).get();

        assertAll(
            () -> assertThat(trustVote.getRelatableStand().getFirstStandId()).isEqualTo(issueStands.get(0).getId()),
            () -> assertThat(trustVote.getRelatableStand().isFirstRelatable()).isTrue(),
            () -> assertThat(trustVote.getRelatableStand().getSecondStandId()).isEqualTo(issueStands.get(1).getId()),
            () -> assertThat(trustVote.getRelatableStand().isSecondRelatable()).isFalse()
        );
    }

    @Test
    void 재가공_이슈에_신뢰도_투표를_업데이트한다() {
        // given
        final ReprocessedIssue issue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", Category.ECONOMY));
        final List<IssueStand> issueStands = issueStandRepository.saveAll(List.of(
            IssueStand.forSave("찬성", issue.getId()),
            IssueStand.forSave("반대", issue.getId())
        ));
        final long memberId = 1L;
        final TrustVoteRequest request = new TrustVoteRequest(issueStands.get(0).getId(), true,
                                                              issueStands.get(1).getId(), false);
        reprocessedIssueTrustVoteRepository.save(
            ReprocessedIssueTrustVote.forSave(issue.getId(), memberId,
                                              new RelatableStand(issueStands.get(0).getId(), true,
                                                                 issueStands.get(1).getId(), true)));

        // when
        reprocessedIssueService.vote(memberId, issue.getId(), request);

        // then
        final ReprocessedIssueTrustVote trustVote = reprocessedIssueTrustVoteRepository.findByReprocessedIssueIdAndMemberId(
            issue.getId(), memberId).get();

        assertAll(
            () -> assertThat(trustVote.getRelatableStand().getFirstStandId()).isEqualTo(issueStands.get(0).getId()),
            () -> assertThat(trustVote.getRelatableStand().isFirstRelatable()).isTrue(),
            () -> assertThat(trustVote.getRelatableStand().getSecondStandId()).isEqualTo(issueStands.get(1).getId()),
            () -> assertThat(trustVote.getRelatableStand().isSecondRelatable()).isFalse()
        );
    }

    @Test
    void 동일한_카테고리의_최신_재가공_이슈_3개를_조회한다() {
        // given
        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", Category.ECONOMY,
                                     Clock.fixed(Instant.parse("2024-03-18T00:00:00Z"), ZoneId.systemDefault())));
        final ReprocessedIssue issue2 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목2", "image", "이미지 캡션", Category.ECONOMY,
                                     Clock.fixed(Instant.parse("2024-03-18T05:00:00Z"), ZoneId.systemDefault())));
        final ReprocessedIssue issue3 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목3", "image", "이미지 캡션", Category.ECONOMY,
                                     Clock.fixed(Instant.parse("2024-03-18T08:00:00Z"), ZoneId.systemDefault())));
        final ReprocessedIssue issue4 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목4", "image", "이미지 캡션", Category.ECONOMY,
                                     Clock.fixed(Instant.parse("2024-03-18T12:00:00Z"), ZoneId.systemDefault())));
        final ReprocessedIssue issue5 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목5", "image", "이미지 캡션", Category.ECONOMY,
                                     Clock.fixed(Instant.parse("2024-03-18T15:00:00Z"), ZoneId.systemDefault())));

        // when
        final List<RecentReprocessedIssueByCategoryResponse> responses = reprocessedIssueService.findReprocessedIssuesByCategory(
            issue1.getId(), Category.ECONOMY.name());

        // then
        assertAll(
            () -> assertThat(responses).hasSize(3),
            () -> assertThat(responses).extracting("id")
                .containsExactlyInAnyOrder(issue5.getId(), issue4.getId(), issue3.getId())
        );
    }

    @Test
    void 재가공_이슈의_신뢰도_투표_결과를_조회한다() {
        // given
        final ReprocessedIssue issue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", Category.ECONOMY));
        final List<IssueStand> issueStands = issueStandRepository.saveAll(List.of(
            IssueStand.forSave("찬성", issue.getId()),
            IssueStand.forSave("반대", issue.getId())
        ));
        final long memberId = 1L;
        reprocessedIssueTrustVoteRepository.save(
            ReprocessedIssueTrustVote.forSave(issue.getId(), memberId,
                                              new RelatableStand(issueStands.get(0).getId(), true,
                                                                 issueStands.get(1).getId(), false)));

        saveAndClearEntityManager();

        // when
        final ReprocessedIssueVoteResultResponse response = reprocessedIssueService.getVoteResult(issue.getId());

        // then
        assertAll(
            () -> assertThat(response.getMostVotedCount()).isEqualTo(1),
            () -> assertThat(response.getMostVotedStand()).isEqualTo("찬성"),
            () -> assertThat(response.getTotalVoteCount()).isEqualTo(1),
            () -> assertThat(response.getVoteRankings().stream()
                                 .filter(vote -> vote.getStand().equals(issueStands.get(0).getStand()))
                                 .findFirst()
                                 .get().getRelatablePercentage())
                .isEqualTo(100),
            () -> assertThat(response.getVoteRankings().stream()
                                 .filter(vote -> vote.getStand().equals(issueStands.get(1).getStand()))
                                 .findFirst()
                                 .get().getRelatablePercentage())
                .isEqualTo(0)
        );
    }
}
