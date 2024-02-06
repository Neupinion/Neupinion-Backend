package com.neupinion.neupinion.issue.application.viewmode;

import com.neupinion.neupinion.issue.application.FollowUpIssueService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StrategyConfig {

    @Bean
    public Map<String, FollowUpIssueViewStrategy> strategies(FollowUpIssueService followUpIssueService) {
        final Map<String, FollowUpIssueViewStrategy> strategies = new HashMap<>();
        strategies.put(ViewMode.ALL.name(), new AllIssueStrategy(followUpIssueService));
        strategies.put(ViewMode.VOTED.name(), new VotedIssueStrategy(followUpIssueService));

        return strategies;
    }
}
