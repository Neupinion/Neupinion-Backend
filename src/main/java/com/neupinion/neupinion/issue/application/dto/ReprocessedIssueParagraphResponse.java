package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "재가공 이슈 단락 응답")
public class ReprocessedIssueParagraphResponse {

    @Schema(description = "재가공 이슈 단락 ID", example = "1")
    private final Long id;

    @Schema(description = "재가공 이슈 단락", example = "블룸버그통신 등에 따르면 22일(현지 시간) 오전 9시를 전후로 미 워싱턴DC에 있는 펜타곤으로 보이는 건물에서 검은 연기가 피어오르는 사진이 트위터를 통해 국내외로 빠르게 확산했다.")
    private final String paragraph;

    @Schema(description = "재가공 이슈 단락 선택 여부", example = "true")
    private final boolean selected;

    public static ReprocessedIssueParagraphResponse of(final ReprocessedIssueParagraph paragraph) {
        return new ReprocessedIssueParagraphResponse(paragraph.getId(), paragraph.getContent(),
                                                     paragraph.isSelectable());
    }
}
