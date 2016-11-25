package com.breedish.bet;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zenind
 */
@Configuration
@EnableConfigurationProperties(BetProperties.class)
public class BetConfiguration {

    @Autowired
    private BetProperties betProperties;

    @Bean
    public MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

    @Bean
    @Qualifier("request-meter")
    public Meter requestMeter() {
        return metricRegistry().meter(betProperties.getGraphiteApiKey() + ".save-meter");
    }

    @Bean
    @Qualifier("request-counter")
    public Counter requestCounter() {
        return metricRegistry().counter(betProperties.getGraphiteApiKey() + ".save-counter");
    }

    @Bean
    public Graphite graphite() {
        return new Graphite(new InetSocketAddress(betProperties.getGraphiteHost(), betProperties.getGraphitePort()));
    }

    @Bean
    public GraphiteReporter graphiteReporter() {
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry())
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .filter(MetricFilter.ALL)
            .build(graphite());
        reporter.start(1, TimeUnit.MINUTES);
        return reporter;
    }
}
