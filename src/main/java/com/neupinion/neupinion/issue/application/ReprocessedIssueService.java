package com.neupinion.neupinion.issue.application;

import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.ShortReprocessedIssueResponse;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTag;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTagRepository;
import com.neupinion.neupinion.issue.domain.repository.dto.ReprocessedIssueWithCommentCount;
import com.neupinion.neupinion.issue.exception.ReprocessedIssueException.ReprocessedIssueNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    private final ReprocessedIssueRepository reprocessedIssueRepository;
    private final ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;
    private final ReprocessedIssueTagRepository reprocessedIssueTagRepository;

    @Transactional
    public Long createReprocessedIssue(final ReprocessedIssueCreateRequest request) {
        final ReprocessedIssue reprocessedIssue = ReprocessedIssue.forSave(request.getTitle(),
                                                                           request.getImageUrl(),
                                                                           request.getCaption(),
                                                                            request.getOriginUrl(),
                                                                           Category.from(request.getCategory()));

        return reprocessedIssueRepository.save(reprocessedIssue).getId();
    }

    public List<ShortReprocessedIssueResponse> findReprocessedIssues(final String dateFormat) {
        final LocalDate targetDate = LocalDate.parse(dateFormat, FORMATTER);
        final PageRequest pageRequest = PageRequest.of(0, REPROCESSED_ISSUES_SIZE, Sort.by("createdAt").descending());
        final List<ReprocessedIssueWithCommentCount> issuesWithCommentCount = reprocessedIssueRepository.findByCreatedAt(
            targetDate, pageRequest);

        return ShortReprocessedIssueResponse.of(issuesWithCommentCount);
    }

    public ReprocessedIssueResponse findReprocessedIssue(final Long id) {
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.findById(id)
            .orElseThrow(ReprocessedIssueNotFoundException::new);
        final List<ReprocessedIssueParagraph> paragraphs = reprocessedIssueParagraphRepository.findByReprocessedIssueIdOrderById(
            id);
        final List<String> tags = reprocessedIssueTagRepository.findByReprocessedIssueId(id).stream()
            .map(ReprocessedIssueTag::getTag)
            .toList();

        return ReprocessedIssueResponse.of(reprocessedIssue, paragraphs, tags);
    }
}
