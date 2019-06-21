package org.adlerzz.apollo.calc.utils;

import java.util.Date;

import static org.adlerzz.apollo.app.Param.TIME_MEASUREMENTS;

public class TimeMeasurements {
    private boolean enabled;
    private long startTime;

    public TimeMeasurements() {
        this.enabled = TIME_MEASUREMENTS.getBoolean();
    }

    public TimeMeasurements(boolean enabled) {
        this.enabled = enabled;
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
