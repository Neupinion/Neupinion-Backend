package com.neupinion.neupinion.query_mode.config;

import com.neupinion.neupinion.issue.application.FollowUpIssueService;
import com.neupinion.neupinion.query_mode.view.follow_up_issue.AllIssueStrategy;
import com.neupinion.neupinion.query_mode.view.follow_up_issue.FollowUpIssueViewStrategy;
import com.neupinion.neupinion.query_mode.view.follow_up_issue.ViewMode;
import com.neupinion.neupinion.query_mode.view.follow_up_issue.VotedIssueStrategy;
import com.neupinion.neupinion.query_mode.view.opinion.OpinionViewMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ViewStrategyConfig {

    @Bean
    public Map<ViewMode, FollowUpIssueViewStrategy> strategies(FollowUpIssueService followUpIssueService) {
        final Map<ViewMode, FollowUpIssueViewStrategy> strategies = new HashMap<>();
        strategies.put(ViewMode.ALL, new AllIssueStrategy(followUpIssueService));
        strategies.put(ViewMode.VOTED, new VotedIssueStrategy(followUpIssueService));

        return strategies;
    }

    @Bean
    public Map<OpinionViewMode, List<Boolean>> opinionViewStrategies() {
        return Map.of(
            OpinionViewMode.ALL, List.of(true, false),
            OpinionViewMode.DOUBT, List.of(false),
            OpinionViewMode.TRUST, List.of(true)
        );
    }
}
