package com.neupinion.neupinion.issue.domain.repository.dto;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;

public interface FollowUpIssueWithReprocessedIssueTitle {

    FollowUpIssue getFollowUpIssue();

    String getReprocessedIssueTitle();
}
