package com.breedish.bet;

import com.codahale.metrics.graphite.Graphite;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zenind
 */
@Configuration
@Import(BetConfiguration.class)
public class BetTestConfiguration {

    @Bean
    public FtpUploader ftpUploader() {
        return Mockito.mock(FtpUploader.class);
    }

    @Bean
    public Graphite graphite() {
        return Mockito.mock(Graphite.class);
    }
}
