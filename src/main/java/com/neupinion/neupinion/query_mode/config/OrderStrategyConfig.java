package com.neupinion.neupinion.query_mode.config;

import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import com.neupinion.neupinion.query_mode.order.OpinionOrderStrategy;
import com.neupinion.neupinion.query_mode.order.OrderMode;
import com.neupinion.neupinion.query_mode.order.PopularOpinionOrderStrategy;
import com.neupinion.neupinion.query_mode.order.RecentOpinionOrderStrategy;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class OrderStrategyConfig {

    private final ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @Bean
    public Map<OrderMode, OpinionOrderStrategy> orderStrategies() {
        return Map.of(
            OrderMode.POPULAR, new PopularOpinionOrderStrategy(reprocessedIssueOpinionRepository),
            OrderMode.RECENT, new RecentOpinionOrderStrategy(reprocessedIssueOpinionRepository)
        );
    }
}
