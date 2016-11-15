package com.breedish.bet;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zenind
 */
@Configuration
public class BestTestConfiguration {

    @Bean
    public FtpUploader ftpUploader() {
        return Mockito.mock(FtpUploader.class);
    }
}
