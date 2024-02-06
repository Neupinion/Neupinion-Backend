package com.neupinion.neupinion.issue.domain.repository.dto;

import com.neupinion.neupinion.issue.domain.ReprocessedIssue;

public interface ReprocessedIssueWithCommentCount {

    ReprocessedIssue getReprocessedIssue();

    Long getCommentCount();
}
