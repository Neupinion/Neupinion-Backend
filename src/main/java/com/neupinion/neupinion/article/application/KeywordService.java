package com.neupinion.neupinion.article.application;

import com.neupinion.neupinion.article.application.dto.KeywordCreateRequest;
import com.neupinion.neupinion.article.application.dto.KeywordResponse;
import com.neupinion.neupinion.article.domain.IssueKeyword;
import com.neupinion.neupinion.article.domain.repository.IssueKeywordRepository;
import com.neupinion.neupinion.issue.domain.IssueStand;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.repository.IssueStandRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class KeywordService {

    private final ChatGptService chatGptService;
    private final IssueKeywordRepository issueKeywordRepository;
    private final ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;
    private final IssueStandRepository issueStandRepository;

    @Transactional
    public void registerKeywords(final KeywordCreateRequest request, final Long issueId) {
        final List<IssueStand> stands = issueStandRepository.findByIssueIdOrderById(issueId);
        issueKeywordRepository.saveAll(
            request.getFirstKeywords().stream()
                .map(keyword -> IssueKeyword.forSave(keyword, stands.get(0).getId(), issueId))
                .collect(Collectors.toList())
        );
        issueKeywordRepository.saveAll(
            request.getSecondKeywords().stream()
                .map(keyword -> IssueKeyword.forSave(keyword, stands.get(1).getId(), issueId))
                .collect(Collectors.toList())
        );
    }

    @Transactional
    public KeywordResponse getKeywords(final Long issueId) {
        final List<IssueStand> stands = issueStandRepository.findByIssueIdOrderById(issueId);
        return extractKeyword(issueId, stands);
    }

    private KeywordResponse extractKeyword(final Long issueId, final List<IssueStand> stands) {
        final String articleBody = reprocessedIssueParagraphRepository.findByReprocessedIssueIdOrderById(issueId)
            .stream()
            .map(ReprocessedIssueParagraph::getContent)
            .collect(Collectors.joining("\n"));

        final KeywordResponse response = chatGptService.getKeywords(articleBody, stands)
            .block();
        response.getFirstKeywords().forEach(keyword -> issueKeywordRepository.save(
            IssueKeyword.forSave(keyword, stands.get(0).getId(), issueId)
        ));
        response.getSecondKeywords().forEach(keyword -> issueKeywordRepository.save(
            IssueKeyword.forSave(keyword, stands.get(1).getId(), issueId)
        ));

        return response;
    }
}
