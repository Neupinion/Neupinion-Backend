package com.neupinion.neupinion.globalexception;

import com.neupinion.neupinion.issue.exception.IssueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        IssueException.class
    })
    public ResponseEntity<ErrorResponse> handleGlobalBadRequestException(final CustomException e) {
        log.error(e.getErrorInfoLog());

        return ResponseEntity.badRequest().body(ErrorResponse.from(e));
    }

}
