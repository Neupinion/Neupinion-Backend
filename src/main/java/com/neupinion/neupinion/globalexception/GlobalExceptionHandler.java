package com.neupinion.neupinion.globalexception;

import com.neupinion.neupinion.issue.exception.CategoryException;
import com.neupinion.neupinion.issue.exception.FollowUpIssueException;
import com.neupinion.neupinion.issue.exception.IssueException;
import com.neupinion.neupinion.issue.exception.ParagraphException;
import com.neupinion.neupinion.issue.exception.ReprocessedIssueException;
import com.neupinion.neupinion.opinion.exception.OpinionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        IssueException.class, CategoryException.class, FollowUpIssueException.class, ReprocessedIssueException.class,
        OpinionException.class, ParagraphException.class
    })
    public ResponseEntity<ErrorResponse> handleGlobalBadRequestException(final CustomException e) {
        log.error(e.getErrorInfoLog());

        return ResponseEntity.badRequest().body(ErrorResponse.from(e));
    }
}
