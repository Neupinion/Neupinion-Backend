package com.neupinion.neupinion.bookmark.ui;

import com.neupinion.neupinion.bookmark.application.BookmarkService;
import com.neupinion.neupinion.bookmark.application.dto.ReprocessedIssueBookmarkRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/reprocessed-issue/{id}/bookmark")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PutMapping
    public ResponseEntity<Void> register(
        @PathVariable final Long id,
        @RequestBody @Valid final ReprocessedIssueBookmarkRequest request
    ) {
        bookmarkService.register(1L, id, request); // TODO: 3/25/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.ok().build();
    }
}
