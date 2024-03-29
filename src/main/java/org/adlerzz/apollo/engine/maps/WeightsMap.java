package org.adlerzz.apollo.engine.maps;

import org.adlerzz.apollo.engine.singles.HSV;
import org.adlerzz.apollo.engine.utils.HSVUtils;
import org.adlerzz.apollo.app.measuretime.MeasureTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WeightsMap {
    private final HashMap<HSV, Map<HSV, Integer>> weightsMap;

    private HSVUtils hsvUtils;

    public WeightsMap() {
        this.weightsMap = new HashMap<>();
    }

    @MeasureTime
    public void makeMap(Image image){

        final int[] frequencies = new int[0x00FFFFFF];

        image.getHSVMap().parallelStream().forEach( pix -> frequencies[pix.hashCode()]++ );

        this.weightsMap.clear();
        for(int i = 0; i < frequencies.length; i++){
            if(frequencies[i] > 0) {
                HSV pix = new HSV(i);
                HSV radix = hsvUtils.reduceColor(pix);

                this.weightsMap.putIfAbsent(radix, new HashMap<>());
                this.weightsMap.get(radix).put(pix, frequencies[i]);
            }
        }
    }

    public HashMap<HSV, Map<HSV, Integer>> getWeightsMap() {
        return weightsMap;
    }

    @Autowired
    public void setHsvUtils(HSVUtils hsvUtils) {
        this.hsvUtils = hsvUtils;
    }
}
