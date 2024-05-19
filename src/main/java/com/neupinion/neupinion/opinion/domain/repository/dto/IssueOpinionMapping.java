package com.neupinion.neupinion.opinion.domain.repository.dto;

import java.time.LocalDateTime;

public record IssueOpinionMapping(Long id, Long paragraphId, Long issueId, Long writerId, String content,
                                  Boolean isReliable, String issueType, LocalDateTime createdAt, Integer likeCount) {

}
