package com.neupinion.neupinion.opinion.application.dto;

import com.neupinion.neupinion.issue.domain.FollowUpIssueParagraph;
import com.neupinion.neupinion.issue.domain.IssueType;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "이슈 의견 응답")
@AllArgsConstructor
@Getter
public class IssueOpinionResponse {

    @Schema(description = "이슈 종류", example = "REPROCESSED")
    private final String issueType;

    @Schema(description = "이슈 ID", example = "1")
    private final long issueId;

    @Schema(description = "이슈 의견 ID", example = "1")
    private final long opinionId;

    @Schema(description = "의견을 작성한 멤버의 ID", example = "1")
    private final long memberId;

    @Schema(description = "의견을 작성한 멤버의 닉네임", example = "김철수")
    private final String nickname;

    @Schema(description = "의견을 작성한 멤버의 프로필 이미지 URL", example = "https://www.neupinion.com/profile/1")
    private final String profileImageUrl;

    @Schema(description = "의견의 근거로 사용되는 문단 id", example = "20")
    private final long paragraphId;

    @Schema(description = "의견의 근거로 사용되는 문단의 내용", example = "이 부분이 문제가 되는 이유는...")
    private final String paragraphContent;

    @Schema(description = "신뢰가 있는 문단 여부", example = "true")
    private final boolean isReliable;

    @Schema(description = "의견 내용", example = "이런 부분은 문제가 있어요!")
    private final String content;

    @Schema(description = "의견의 좋아요 수", example = "10")
    private final int likeCount;

    @Schema(description = "현재 멤버의 좋아요 여부", example = "true")
    private final boolean isLiked;

    @Schema(description = "의견을 작성한 날짜", example = "2024-05-11T12:34:56")
    private final LocalDateTime createdAt;

    public static IssueOpinionResponse of(final ReprocessedIssueOpinion opinion, final Member member,
                                          final ReprocessedIssueParagraph paragraph,
                                          final boolean isLiked) {
        return new IssueOpinionResponse(IssueType.REPROCESSED.name(), opinion.getReprocessedIssueId(), opinion.getId(),
                                        opinion.getMemberId(), member.getNickname(), member.getProfileImageUrl(),
                                        opinion.getParagraphId(), paragraph.getContent(), opinion.getIsReliable(),
                                        opinion.getContent(), opinion.getLikes().size(), isLiked,
                                        opinion.getCreatedAt());
    }

    public static IssueOpinionResponse of(final FollowUpIssueOpinion opinion, final Member member,
                                          final FollowUpIssueParagraph paragraph, final boolean isLiked) {
        return new IssueOpinionResponse(IssueType.FOLLOW_UP.name(), opinion.getFollowUpIssueId(), opinion.getId(),
                                        opinion.getMemberId(), member.getNickname(), member.getProfileImageUrl(),
                                        opinion.getParagraphId(), paragraph.getContent(), opinion.getIsReliable(),
                                        opinion.getContent(), opinion.getLikes().size(), isLiked,
                                        opinion.getCreatedAt());
    }

    public boolean getIsLiked() {
        return isLiked;
    }

    public boolean getIsReliable() {
        return isReliable;
    }
}
