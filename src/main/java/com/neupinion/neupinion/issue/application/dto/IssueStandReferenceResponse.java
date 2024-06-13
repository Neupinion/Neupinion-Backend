package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.IssueStand;
import com.neupinion.neupinion.issue.domain.IssueStandReference;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(name = "이슈 입장 레퍼런스 응답")
@Getter
@AllArgsConstructor
public class IssueStandReferenceResponse {

    @Schema(name = "재가공 이슈 입장", example = "민희진")
    private String stand;

    @Schema(name = "레퍼런스 URL 리스트")
    private List<String> sources;

    public static IssueStandReferenceResponse of(final IssueStand issueStand, final List<IssueStandReference> sources) {
        final List<String> filteredSources = sources.stream()
            .filter(source -> source.getIssueStandId().equals(issueStand.getId()))
            .map(IssueStandReference::getReferenceUrl)
            .toList();

        return new IssueStandReferenceResponse(issueStand.getStand(), filteredSources);
    }
}
