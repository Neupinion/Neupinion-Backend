package com.neupinion.neupinion.issue.application;

import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.IssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.exception.IssueException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReprocessedIssueService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int REPROCESSED_ISSUES_SIZE = 4;

    private final IssueRepository issueRepository;
    private final ReprocessedIssueRepository reprocessedIssueRepository;

    public Long save(final ReprocessedIssueCreateRequest request) {
        validateIssueId(request.getIssueId());
        final ReprocessedIssue reprocessedIssue = ReprocessedIssue.forSave(request.getTitle(),
                                                                           request.getImageUrl(),
                                                                           Category.from(request.getCategory()),
                                                                           request.getIssueId());
        return reprocessedIssueRepository.save(reprocessedIssue).getId();
    }

    private void validateIssueId(final Long issueId) {
        if (!issueRepository.existsById(issueId)) {
            throw new IssueException.IssueNotExistException(Map.of("issueId", issueId.toString()));
        }
    }

    public List<ReprocessedIssueResponse> findReprocessedIssues(final String dateFormat) {
        final LocalDate targetDate = LocalDate.parse(dateFormat, FORMATTER);
        final LocalDateTime targetDateTime = targetDate.atStartOfDay();
        final PageRequest pageRequest = PageRequest.of(0, REPROCESSED_ISSUES_SIZE, Sort.by("createdAt").descending());
        final List<ReprocessedIssue> reprocessedIssues = reprocessedIssueRepository.findByCreatedAt(targetDateTime,
                                                                                                    pageRequest);
        return ReprocessedIssueResponse.of(reprocessedIssues);
    }
}
