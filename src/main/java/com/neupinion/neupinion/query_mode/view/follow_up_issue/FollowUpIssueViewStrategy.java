package com.neupinion.neupinion.query_mode.view.follow_up_issue;

import com.neupinion.neupinion.issue.application.dto.FollowUpIssueByCategoryResponse;
import java.util.List;

public interface FollowUpIssueViewStrategy {

    List<FollowUpIssueByCategoryResponse> findIssueByCategoryAndDate(String dateFormat, String category, Long memberId);
}
