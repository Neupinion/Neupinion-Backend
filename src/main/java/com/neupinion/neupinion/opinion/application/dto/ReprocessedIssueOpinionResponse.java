package com.neupinion.neupinion.opinion.application.dto;

import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "재가공 이슈 의견 응답")
@AllArgsConstructor
@Getter
public class ReprocessedIssueOpinionResponse {

    @Schema(description = "재가공 이슈 의견 ID", example = "1")
    private final long id;

    @Schema(description = "의견을 작성한 멤버의 ID", example = "1")
    private final long memberId;

    @Schema(description = "의견을 작성한 멤버의 닉네임", example = "김철수")
    private final String nickname;

    @Schema(description = "의견을 작성한 멤버의 프로필 이미지 URL", example = "https://www.neupinion.com/profile/1")
    private final String profileImageUrl;

    @Schema(description = "의견을 작성한 날짜", example = "2024-04-02T12:34:56")
    private final LocalDateTime createdAt;

    @Schema(description = "신뢰가 있는 문단 여부", example = "true")
    private final boolean isReliable;

    @Schema(description = "의견의 근거로 사용되는 문단 id", example = "20")
    private final long paragraphId;

    @Schema(description = "의견의 근거로 사용되는 문단의 내용", example = "이 부분이 문제가 되는 이유는...")
    private final String paragraphContent;

    @Schema(description = "의견 내용", example = "이런 부분은 문제가 있어요!")
    private final String content;

    @Schema(description = "의견의 좋아요 수", example = "10")
    private final int likeCount;

    @Schema(description = "현재 멤버의 좋아요 여부", example = "true")
    private final boolean isLiked;

    public static ReprocessedIssueOpinionResponse of(final ReprocessedIssueOpinion opinion, final int likeCount,
                                                     final Member member, final ReprocessedIssueParagraph paragraph,
                                                     final boolean isLiked) {
        return new ReprocessedIssueOpinionResponse(opinion.getId(), opinion.getMemberId(),
                                                   member.getNickname(), member.getProfileImageUrl(),
                                                   opinion.getCreatedAt(), opinion.isReliable(),
                                                   opinion.getParagraphId(), paragraph.getContent(),
                                                   opinion.getContent(), likeCount, isLiked);
    }

    public boolean getIsReliable() {
        return isReliable;
    }

    public boolean getIsLiked() {
        return isLiked;
    }
}
