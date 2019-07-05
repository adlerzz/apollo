package org.adlerzz.apollo.app.measuretime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

import static org.adlerzz.apollo.app.param.Param.TIME_MEASUREMENT;

@Aspect
@Component
@Scope("prototype")
public class MeasureTimeProcessor {

    private static final Logger log = LoggerFactory.getLogger(MeasureTimeProcessor.class);

    private long time;
    private boolean enabled;


    @Pointcut("@annotation(MeasureTime) && execution(@MeasureTime * *(..))")
    public void pointcut(){}

    @Before("pointcut()")
    public void beforeProcess(JoinPoint joinPoint){
        this.enabled = TIME_MEASUREMENT.getValue();
        if(this.enabled) {
            this.time = (new Date()).getTime();

        }
    }

    @After("pointcut()")
    public void afterProcess(JoinPoint joinPoint){
        if(this.enabled) {
            this.time = (new Date()).getTime() - this.time;
            Method method =((MethodSignature)joinPoint.getSignature()).getMethod();
            String name = method.getName();
            log.info(" Done {} in {} ms", name, this.time);
        }
    }
}
