package com.breedish.bet;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zenind
 */
@Component
@ConfigurationProperties(prefix = "bet")
@Data
public class BetProperties {

    private String storagePath;

    private Integer cacheSize;

    private Integer cacheTime;

    private String ftpServer;

    private String ftpUser;

    private String ftpPassword;

    private String ftpStoragePath;
}
