package org.zion.apollo.utils;

import org.zion.apollo.data.HSV;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.zion.apollo.utils.Constants.*;

public class HSVUtilities {

    private static HSVUtilities INSTANCE = null;
    private HSVUtilities(){}

    public static HSVUtilities getInstance(){
        if(INSTANCE == null){
            INSTANCE = new HSVUtilities();
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


}
