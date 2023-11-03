package org.sterl.db_grundlagen;

import java.time.Duration;

import lombok.Data;

@Data
public class TimeMeasure {
    long time;
    
    public TimeMeasure() {
        start();
    }
    public void start() {
        time = System.nanoTime();
    }
    
    public Duration stop() {
        time = System.nanoTime() - time;
        return Duration.ofNanos(time);
    }
    public void stopAndPrintMs() {
        Duration d = stop();
        print("Runtime", d);
    }
    public static void print(String what, Duration d) {
        System.err.println(what + "=" + d.toMillis() + "ms");
    }
}
