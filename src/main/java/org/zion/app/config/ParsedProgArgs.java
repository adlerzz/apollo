package org.zion.app.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.zion.app.config.Param.*;

public class ParsedProgArgs {

    private Map<Param, Object> params;
    private Map<String, Param> flags;

    private static ParsedProgArgs INSTANCE = null;
    private ParsedProgArgs(){
        this.params = new HashMap<>();

        this.flags = new HashMap<>();
        this.flags.put("-i", INPUT_IMAGE);
        this.flags.put("--input-image", INPUT_IMAGE);
        this.flags.put("-r", REDUCED_IMAGE);
        this.flags.put("--reduced-image", REDUCED_IMAGE);
        this.flags.put("-p", PALETTE_IMAGE);
        this.flags.put("--palette-image", PALETTE_IMAGE);
        this.flags.put("-f", PALETTE_FORMAT);
        this.flags.put("--palette-format", PALETTE_FORMAT);
    }

    public static ParsedProgArgs getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ParsedProgArgs();
        }
        return INSTANCE;
    }

    public void parse(String[] args){
        int i = 0;
        while(i < args.length){
            Param key = this.flags.get(args[i]);
            if(key != null){
                if(i + 1 < args.length){
                    String value = args[i + 1];
                    params.put(key, value);
                }
            }
            i++;
        }
    }

    public Object getParam(Param param){
        return this.params.get(param);
    }

    public Optional<String> getStringParam(Param param){
        return Optional.ofNullable( (String)this.params.get(param) );
    }

    public String getStringParamOrNull(Param param){
        return (String)this.params.get(param);
    }

    public void fillIfEmptyParam(Param param, Object value){
        this.params.putIfAbsent(param, value);
    }

}
