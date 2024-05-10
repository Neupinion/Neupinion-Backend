package com.neupinion.neupinion.viewmode.config;

import com.neupinion.neupinion.issue.application.FollowUpIssueService;
import com.neupinion.neupinion.opinion.application.OpinionService;
import com.neupinion.neupinion.viewmode.follow_up_issue.AllIssueStrategy;
import com.neupinion.neupinion.viewmode.follow_up_issue.FollowUpIssueViewStrategy;
import com.neupinion.neupinion.viewmode.follow_up_issue.ViewMode;
import com.neupinion.neupinion.viewmode.follow_up_issue.VotedIssueStrategy;
import com.neupinion.neupinion.viewmode.opinion.AllOpinionStrategy;
import com.neupinion.neupinion.viewmode.opinion.OpinionViewMode;
import com.neupinion.neupinion.viewmode.opinion.OpinionViewStrategy;
import com.neupinion.neupinion.viewmode.opinion.ReliableOpinionStrategy;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StrategyConfig {

    @Bean
    public Map<ViewMode, FollowUpIssueViewStrategy> strategies(FollowUpIssueService followUpIssueService) {
        final Map<ViewMode, FollowUpIssueViewStrategy> strategies = new HashMap<>();
        strategies.put(ViewMode.ALL, new AllIssueStrategy(followUpIssueService));
        strategies.put(ViewMode.VOTED, new VotedIssueStrategy(followUpIssueService));

        return strategies;
    }

    @Bean
    public Map<OpinionViewMode, OpinionViewStrategy> opinionViewStrategies(OpinionService opinionService) {
        final Map<OpinionViewMode, OpinionViewStrategy> strategies = new HashMap<>();
        final ReliableOpinionStrategy reliableOpinionStrategy = new ReliableOpinionStrategy(opinionService);
        strategies.put(OpinionViewMode.ALL, new AllOpinionStrategy(opinionService));
        strategies.put(OpinionViewMode.DOUBT, reliableOpinionStrategy);
        strategies.put(OpinionViewMode.TRUST, reliableOpinionStrategy);

        return strategies;
    }
}
