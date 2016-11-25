package com.breedish.bet.monitoring;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author zenind
 */
@Component
public class Monitoring {

    @Autowired
    @Qualifier("request-counter")
    private Counter requestCounter;

    @Autowired
    @Qualifier("request-meter")
    private Meter requestMeter;

    public void incrementCount() {
        requestCounter.inc();
    }

    public void mark() {
        requestMeter.mark();
    }
}
