package com.breedish.bet;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zenind
 */
@Component
public class FtpUploader {

    private static final Logger LOG = LoggerFactory.getLogger(FtpUploader.class);

    @Autowired
    private BetProperties betProperties;

    public void uploadData(InputStream is, String filename) {
        StopWatch watch = new StopWatch();
        watch.start();
        LOG.info("Start upload of {0}", filename);
        System.out.println(filename);
        FTPClient client = new FTPClient();
        try {
            client.connect(betProperties.getFtpServer());
            client.login(betProperties.getFtpUser(), betProperties.getFtpPassword());
            client.storeFile(new File(betProperties.getFtpDataDirectory(), filename).toString(), is);
            client.logout();
        } catch (IOException e) {
            System.out.println(e);
            LOG.error("Issue uploading file {0}. Reason {1}", filename, e);
        } finally {
            try {
                client.disconnect();
            } catch (Exception e) {
                LOG.error("Issue uploading file {0}. Reason {1}", filename, e);
            }
        }
        watch.stop();
        LOG.info("Upload of {0} was done in {1}", filename, watch.getLastTaskInfo().getTimeMillis());
    }
}
