package com.neupinion.neupinion.article.application;

import com.neupinion.neupinion.article.application.dto.KeywordResponse;
import com.neupinion.neupinion.article.domain.IssueKeyword;
import com.neupinion.neupinion.article.domain.repository.IssueKeywordRepository;
import com.neupinion.neupinion.issue.domain.IssueStand;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.repository.IssueStandRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import java.util.List;
import java.util.Objects;
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
    public KeywordResponse getKeywords(final Long issueId) {
        final List<IssueKeyword> keywords = issueKeywordRepository.findByIssueId(issueId);
        final List<IssueStand> stands = issueStandRepository.findByIssueIdOrderById(issueId);
        if (keywords.isEmpty()) {
            return extractKeyword(issueId, stands);
        }
        final IssueStand firstStand = stands.get(0);
        final IssueStand secondStand = stands.get(1);
        return new KeywordResponse(
            firstStand.getStand(),
            keywords.stream()
                    .filter(keyword -> Objects.equals(keyword.getIssueStandId(), firstStand.getId()))
                    .map(IssueKeyword::getKeyword)
                    .toList(),
            secondStand.getStand(),
            keywords.stream()
                    .filter(keyword -> Objects.equals(keyword.getIssueStandId(), secondStand.getId()))
                    .map(IssueKeyword::getKeyword)
                    .toList());
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
