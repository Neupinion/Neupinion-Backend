package com.neupinion.neupinion.issue.application.viewmode;

import com.neupinion.neupinion.issue.application.dto.FollowUpIssueByCategoryResponse;
import java.util.List;

public interface FollowUpIssueViewStrategy {

    List<FollowUpIssueByCategoryResponse> findIssueByCategoryAndDate(String dateFormat, String category, Long memberId);
}
