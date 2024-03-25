package com.neupinion.neupinion.bookmark.application;

import com.neupinion.neupinion.bookmark.application.dto.ReprocessedIssueBookmarkRequest;
import com.neupinion.neupinion.bookmark.domain.ReprocessedIssueBookmark;
import com.neupinion.neupinion.bookmark.domain.repository.ReprocessedIssueBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final ReprocessedIssueBookmarkRepository reprocessedIssueBookmarkRepository;

    @Transactional
    public void register(final Long memberId, final Long id, final ReprocessedIssueBookmarkRequest request) {
        final ReprocessedIssueBookmark bookmark = reprocessedIssueBookmarkRepository.findByReprocessedIssueIdAndMemberId(
                id, memberId)
            .orElseGet(() -> reprocessedIssueBookmarkRepository.save(ReprocessedIssueBookmark.forSave(id, memberId)));
        bookmark.updateIsBookmarked(request.getIsBookmarked());
    }
}
