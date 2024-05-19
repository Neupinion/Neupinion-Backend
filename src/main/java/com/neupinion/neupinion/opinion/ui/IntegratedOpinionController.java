package com.neupinion.neupinion.opinion.ui;

import com.neupinion.neupinion.opinion.application.IntegratedOpinionService;
import com.neupinion.neupinion.opinion.application.dto.IssueOpinionResponse;
import com.neupinion.neupinion.query_mode.order.OrderMode;
import com.neupinion.neupinion.query_mode.view.opinion.OpinionViewMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/issue/{issueId}/opinion")
@RestController
public class IntegratedOpinionController {

    private final IntegratedOpinionService integratedOpinionService;

    @GetMapping
    public ResponseEntity<List<IssueOpinionResponse>> getOpinions(
        @PathVariable final Long issueId,
        @RequestParam(defaultValue = "RECENT", required = false) final String orderMode,
        @RequestParam(defaultValue = "ALL", required = false) final String viewMode,
        @RequestParam(defaultValue = "0", required = false) final Integer page
    ) {
        final OrderMode orderFilter = OrderMode.from(orderMode);
        final OpinionViewMode viewFilter = OpinionViewMode.from(viewMode);
        return ResponseEntity.ok(
            integratedOpinionService.getIntegratedOpinions(issueId, 1L, orderFilter, viewFilter,
                                                           page));  // TODO: 24. 5. 11. 추후 액세스 토큰 인증 로직 추가하기
    }

    @GetMapping("/top")
    public ResponseEntity<List<IssueOpinionResponse>> getTopOpinion(
        @PathVariable final Long issueId
    ) {
        return ResponseEntity.ok(
            integratedOpinionService.getTopOpinionsByLikes(issueId, 1L));  // TODO: 24. 5. 11. 추후 액세스 토큰 인증 로직 추가하기
    }
}
