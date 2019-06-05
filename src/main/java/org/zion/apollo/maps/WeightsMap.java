package org.zion.apollo.maps;

import org.zion.apollo.data.HSV;
import org.zion.apollo.utils.HSVUtilities;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WeightsMap {
    private final HashMap<HSV, Map<HSV, Integer>> weightsMap;
    private static final HSVUtilities HSV_UTILITIES = HSVUtilities.getInstance();

    public WeightsMap(Image image) {
        long startTime = (new Date()).getTime();
        System.out.print("Creating the map of weights...");
        this.weightsMap = new HashMap<>();

        final int[] frequencies = new int[0x00FFFFFF];

        image.getHSVMap().parallelStream().forEach( pix -> frequencies[pix.hashCode()]++ );


        for(int i =0; i < frequencies.length; i++){
            if(frequencies[i] > 0) {
                HSV pix = new HSV(i);
                HSV radix = HSV_UTILITIES.reduceColor(pix);

                this.weightsMap.putIfAbsent(radix, new HashMap<>());
                this.weightsMap.get(radix).put(pix, frequencies[i]);
            }
        }

        long endTime = (new Date()).getTime();
        System.out.println(" Done in " + (endTime - startTime) + " ms");
    }

    public HashMap<HSV, Map<HSV, Integer>> getWeightsMap() {
        return weightsMap;
    }
}
