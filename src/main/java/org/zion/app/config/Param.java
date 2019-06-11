package org.zion.app.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.zion.apollo.utils.Constants.FORMAT_PNG;

public enum Param {
    INPUT_IMAGE("i", "inputImage"),
    PALETTE_FORMAT("f", "paletteFormat", FORMAT_PNG),
    REDUCED_IMAGE("r", "reducedImage"),
    PALETTE_IMAGE("p", "paletteImage", () -> String.format("%s_palette.%s", INPUT_IMAGE.getValue(), PALETTE_FORMAT.getValue())),
    TIME_MEASUREMENTS("t", "timeMeasurements", true);

    private String shortFlag;
    private String fullFlag;
    private Object value;
    private boolean isBoolean;

    Param(String shortFlag, String fullFlag){
        this.shortFlag = shortFlag;
        this.fullFlag = fullFlag;
        this.value = null;
        this.isBoolean = false;
    }

    Param(String shortFlag, String fullFlag, Object defaultValue) {
        this.shortFlag = shortFlag;
        this.fullFlag = fullFlag;
        this.value = defaultValue;
        this.isBoolean = false;
    }
    Param(String shortFlag, String fullFlag, boolean isBoolean) {
        this.shortFlag = shortFlag;
        this.fullFlag = fullFlag;
        this.value = Boolean.FALSE;
        this.isBoolean = isBoolean;
    }

    Param(String shortFlag, String fullFlag, Supplier<Object> defaultValueFactory) {
        this.shortFlag = shortFlag;
        this.fullFlag = fullFlag;
        this.value = defaultValueFactory.get();
    }

    public static void parse(String[] args){
        final Map<String, Param> flags = new HashMap<>();
        for(Param param: Param.values()){
            flags.put( "-" + param.getShortFlag(), param);
            flags.put( "--" + param.getFullFlag(), param);
        }
        int i = 0;
        while(i < args.length){
            Param key = flags.get(args[i]);
            if(key != null){
                if(key.isBoolean){
                    key.setValue(Boolean.TRUE);
                } else {
                    if (i + 1 < args.length) {
                        String value = args[i + 1];
                        key.setValue(value);
                        i++;
                    }
                }
            }
            i++;
        }
    }

    public String getShortFlag() {
        return shortFlag;
    }

    public String getFullFlag() {
        return fullFlag;
    }

    public void setValue(Object value){
        this.value = value;
    }

    public Object getValue(){
        return value;
    }

    public Optional<Object> getOptValue(){
        return Optional.ofNullable(this.value);
    }

    public Optional<String> getOptString(){
        return Optional.ofNullable(value).map(Object::toString);
    }

    public Boolean getBoolean(){
        return (Boolean) value;
    }
}
