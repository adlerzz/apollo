package org.adlerzz.apollo.engine.maps;

import org.adlerzz.apollo.engine.singles.HSV;
import org.adlerzz.apollo.engine.utils.HSVUtils;
import org.adlerzz.apollo.app.measuretime.MeasureTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReplacingMap {

    private final HashMap<HSV, HSV> replacingMap;

    @Autowired
    private HSVUtils hsvUtils;
    //private static final HSVUtils HSV_UTILS = HSVUtils.getInstance();

    public ReplacingMap() {
        this.replacingMap = new HashMap<>();
    }

    @MeasureTime
    public void makeMap(WeightsMap weightsMap){
        this.replacingMap.clear();
        for(Map.Entry<HSV, Map<HSV, Integer>> weightEl: weightsMap.getWeightsMap().entrySet()) {
            HSV normalized = hsvUtils.normalize(weightEl.getValue());
            this.replacingMap.put( weightEl.getKey(), normalized );
        }
    }


    public HSV getReducingReplacing(HSV value){
        return this.replacingMap.get(hsvUtils.reduceColor(value));
    }
}
