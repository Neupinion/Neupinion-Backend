package com.neupinion.neupinion.opinion.ui;

import com.neupinion.neupinion.opinion.application.IntegratedOpinionService;
import com.neupinion.neupinion.opinion.application.dto.IssueOpinionResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/issue/{issueId}/opinion")
@RestController
public class IntegratedOpinionController {

    private final IntegratedOpinionService integratedOpinionService;

    @GetMapping("/top")
    public ResponseEntity<List<IssueOpinionResponse>> getTopOpinion(
        @PathVariable final Long issueId
    ) {
        return ResponseEntity.ok(
            integratedOpinionService.getTopOpinions(issueId, 1L));  // TODO: 24. 5. 11. 추후 액세스 토큰 인증 로직 추가하기
    }
}
