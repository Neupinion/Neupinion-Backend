package com.neupinion.neupinion.bookmark.ui;

import com.neupinion.neupinion.auth.ui.argumentresolver.Authenticated;
import com.neupinion.neupinion.auth.ui.argumentresolver.MemberInfo;
import com.neupinion.neupinion.bookmark.application.BookmarkService;
import com.neupinion.neupinion.bookmark.application.dto.ReprocessedIssueBookmarkRequest;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @RequestBody @Valid final ReprocessedIssueBookmarkRequest request,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        bookmarkService.register(memberInfo.memberId(), id, request);

        return ResponseEntity.ok().build();
    }
}
