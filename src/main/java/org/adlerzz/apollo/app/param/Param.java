package org.adlerzz.apollo.app.param;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public enum Param {
    PALETTE_FORMAT(true),
    CUT_OFF_THRESHOLD(true),
    TIME_MEASUREMENT(true),
    OUTPUT_VIEW(true),
    BOT_NAME,
    BOT_TOKEN,
    PING_URL;

    private Object value;
    private ArrayList<Object> domain;
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

    public List<String> getDomain(){
        if(this.domain != null){
            return this.domain.stream().map(Object::toString).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private void setDomain(Collection<Object> values){
        this.domain = new ArrayList<>(values);
    }

    private static final Logger log = LoggerFactory.getLogger(Param.class);

    public static void parse(ParamsConfig paramsConfig){
        Map<String, Object> paramsMap = paramsConfig.getMap();
        for(Param param: Param.values()){
            String recognized = param.name();

            if(paramsMap.containsKey(recognized)) {
                Object value = paramsMap.get(recognized);
                log.debug("recognized {} variable with value \u001B[35m\"{}\"\u001B[0m as {}", recognized, value, value.getClass());
                if(value instanceof Map){
                    Map compositeValue = (Map) value;
                    param.setValue(compositeValue.get("default"));
                    param.setDomain(((Map)compositeValue.get("domain")).values());
                } else {
                    param.setValue(value);
                }
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

    public String getString(){
        return this.value.toString();
    }


    public <T> Optional<T> getOptional(){
        return Optional.ofNullable( getValue() );
    }

}
