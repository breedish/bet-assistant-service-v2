package com.breedish.bet;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/api/v2/files")
@SpringBootApplication
@ManagedResource(objectName = "bet:name=BetController")
@EnableConfigurationProperties(BetProperties.class)
public class BetApplication {

    private static Logger LOG = LoggerFactory.getLogger(BetApplication.class);

    static final String X_BET_HEADER = "X-bet-version=v2";

    private BetProperties betProperties;

    private LoadingCache<String, String> filesCache;

    @Autowired
    public BetApplication(BetProperties betProperties) {
        this.betProperties = betProperties;
    }

    @PostConstruct
    public void init() {
        LOG.info("properties {}", betProperties.getStoragePath());
        filesCache = CacheBuilder.newBuilder()
            .maximumSize(betProperties.getCacheSize())
            .recordStats()
            .expireAfterWrite(betProperties.getCacheTime(), TimeUnit.MINUTES)
            .build(
                new CacheLoader<String, String>() {
                    public String load(String file) throws Exception {
                        StringWriter writer = new StringWriter();
                        FileCopyUtils.copy(new BufferedReader(new FileReader(filePath(file))), writer);
                        return writer.toString();
                    }
                });
    }

    @RequestMapping(path = "/{file}", method = POST, headers = {X_BET_HEADER}, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity save(@RequestBody String json, @PathVariable String file) throws IOException {
        validateJson(json);
        FileCopyUtils.copy(json, new FileWriter(filePath(file), false));
        filesCache.invalidate(file);
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{file}", method = GET, headers = {X_BET_HEADER}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get(@PathVariable String file) throws Exception {
        return ok(filesCache.get(file));
    }

    @ManagedOperation
    public Map<Object, Object> getCacheStats() {
        final CacheStats stats = filesCache.stats();

        Map<Object, Object> statsInfo = new HashMap<>();
        statsInfo.put("averageLoadPenalty", stats.averageLoadPenalty());
        statsInfo.put("hitCount", stats.hitCount());
        statsInfo.put("hitRate", stats.hitRate());
        statsInfo.put("loadCount", stats.loadCount());
        statsInfo.put("loadSuccessCount", stats.loadSuccessCount());
        statsInfo.put("loadExceptionCount", stats.loadExceptionCount());
        statsInfo.put("missRate", stats.missRate());
        statsInfo.put("missCount", stats.missCount());
        statsInfo.put("totalLoadTime", stats.totalLoadTime());
        return statsInfo;
    }

    private File filePath(String fileName) {
        return new File(betProperties.getStoragePath(), fileName);
    }

    private void validateJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
            JsonFactory factory = mapper.getFactory();
            JsonParser parser = factory.createParser(json);
            mapper.readTree(parser);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Provided json is not valid. Reason: %s", e.getMessage()));
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(BetApplication.class, args);
    }
}
