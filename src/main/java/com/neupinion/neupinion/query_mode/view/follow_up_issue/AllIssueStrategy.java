package com.neupinion.neupinion.query_mode.view.follow_up_issue;

import com.neupinion.neupinion.issue.application.FollowUpIssueService;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueByCategoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AllIssueStrategy implements FollowUpIssueViewStrategy {

    private final FollowUpIssueService followUpIssueService;

    @Override
    public List<FollowUpIssueByCategoryResponse> findIssueByCategoryAndDate(final String dateFormat,
                                                                            final String category,
                                                                            final Long memberId) {
        return followUpIssueService.findFollowUpIssueByCategoryAndDate(dateFormat, category, memberId);
    }
}
