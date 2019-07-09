package org.adlerzz.apollo.app.param;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public enum Param {
    INPUT_IMAGE,
    PALETTE_FORMAT(true),
    REDUCED_IMAGE,
    CUT_OFF_THRESHOLD(true),
    TIME_MEASUREMENT(true),
    BOT_NAME,
    BOT_TOKEN,
    PING_URL;

    private Object value;
    private boolean editable;

    Param(){
        this.value = null;
        this.editable = false;
    }

    Param(boolean editable){
        this.value = null;
        this.editable = editable;
    }

    public boolean isEditable(){
        return this.editable;
    }

    private static final Logger log = LoggerFactory.getLogger(Param.class);

    public static void parse(ParamsConfig paramsConfig){
        for(Param param: Param.values()){
            String recognized = param.name();
            if(paramsConfig.getMap().containsKey(recognized)) {
                Object value = paramsConfig.getMap().get(recognized);
                log.debug("recognized {} variable with value \u001B[35m\"{}\"\u001B[0m as {}", recognized, value, value.getClass());
                param.setValue(value);
            } else{
                log.debug("{} not recognized", recognized);
            }
        }
    }

    public <T> void setValue(T value){
        this.value = value;
    }

    public <T> T getValue(){
        return (T) this.value;
    }

    public <T> Optional<T> getOptional(){
        return Optional.ofNullable( getValue() );
    }

}
