package org.adlerzz.apollo.engine.utils;

import org.adlerzz.apollo.engine.singles.HSV;
import org.adlerzz.apollo.engine.singles.RGBA;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.adlerzz.apollo.engine.utils.Constants.*;

@Service
public class HSVUtils {

    private static HSVUtils INSTANCE = null;
    private HSVUtils(){}

    public static HSVUtils getInstance(){
        if(INSTANCE == null){
            INSTANCE = new HSVUtils();
        }
        return INSTANCE;
    }

    public HSV reduceColor(HSV color){
        HSV newColor = new HSV();

        newColor.setH( color.getH() & H_REDUCE_MASK );
        newColor.setS( color.getS() & S_REDUCE_MASK );
        newColor.setV( color.getV() & V_REDUCE_MASK );
        return newColor;
    }

    public HSV normalize(Map<HSV, Integer> map){

        long sum = weight(map);
        List<Double> result = map.entrySet()
                .stream()
                .map( e -> Arrays.asList( (double) e.getKey().getH(),
                                          (double) e.getKey().getS(),
                                          (double) e.getKey().getV(),
                                          (double) e.getValue()) )
                .reduce( Arrays.asList(0.0, 0.0, 0.0), (acc, el) -> {
                    acc.set(0, acc.get(0) + el.get(0) * el.get(3));
                    acc.set(1, acc.get(1) + el.get(1) * el.get(3));
                    acc.set(2, acc.get(2) + el.get(2) * el.get(3));
                    return acc;
                });
        HSV res = new HSV();
        res.setH( result.get(0) / sum );
        res.setS( result.get(1) / sum );
        res.setV( result.get(2) / sum );
        return res;
    }

    public int weight(Map<HSV, Integer> map){

        return map.values()
                .stream()
                .reduce(Integer::sum)
                .orElse(0);

    }

    public Color toColor(HSV hsv){
        return new Color( new RGBA(hsv).toRaw());
    }


}
