package org.zion.apollo.utils;

import java.util.Date;

import static org.zion.app.config.Param.TIME_MEASUREMENTS;

public class TimeMeasurements {
    private boolean enabled;
    private long startTime;

    public TimeMeasurements() {
        this.enabled = TIME_MEASUREMENTS.getBoolean();
    }

    public void start(){
        if(this.enabled) {
            this.startTime = (new Date()).getTime();
        }
    }
    public void start(String prompt){
        if(this.enabled) {
            System.out.printf("%s", prompt);
            this.startTime = (new Date()).getTime();
        }
    }

    public void finishAndShowResult(){
        if(this.enabled){
            long finishTime = (new Date()).getTime();
            System.out.printf("Done in %d ms%n", (finishTime - this.startTime));
        }
    }


}
