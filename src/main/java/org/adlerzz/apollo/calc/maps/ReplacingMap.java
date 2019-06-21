package org.adlerzz.apollo.calc.maps;

import org.adlerzz.apollo.calc.singles.HSV;
import org.adlerzz.apollo.calc.utils.HSVUtils;
import org.adlerzz.apollo.calc.utils.TimeMeasurements;

import java.util.HashMap;
import java.util.Map;

public class ReplacingMap {

    private final HashMap<HSV, HSV> replacingMap;
    private static final HSVUtils HSV_UTILS = HSVUtils.getInstance();

    public ReplacingMap(WeightsMap weightsMap) {
        final TimeMeasurements TM = new TimeMeasurements();
        TM.start(" Creating the map of replacing... ");

        this.replacingMap = new HashMap<>();

        for(Map.Entry<HSV, Map<HSV, Integer>> weightEl: weightsMap.getWeightsMap().entrySet()) {
            HSV normalized = HSV_UTILS.normalize(weightEl.getValue());
            this.replacingMap.put( weightEl.getKey(), normalized );
        }
        TM.finishAndShowResult();
    }


    public HSV getReducingReplacing(HSV value){
        return this.replacingMap.get(HSV_UTILS.reduceColor(value));
    }
}
