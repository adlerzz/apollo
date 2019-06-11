package org.zion.app;

import org.zion.apollo.maps.Image;
import org.zion.apollo.maps.Palette;
import org.zion.apollo.maps.ReplacingMap;
import org.zion.apollo.maps.WeightsMap;
import org.zion.apollo.utils.TimeMeasurements;
import org.zion.app.config.Param;


import java.util.Date;

import static org.zion.app.config.Param.*;

public class Main {

    public static void main(String[] args) {

        Param.parse(args);
        TimeMeasurements M = new TimeMeasurements();
        M.start("Start processing at " + (new Date()).toString() + "\n");

        if(!INPUT_IMAGE.getOptValue().isPresent()){
            System.err.println("no input file");
            System.exit(1);
            return;
        }


        Image image = new Image();
        INPUT_IMAGE.getOptString().ifPresent(image::loadFromBMP);

        if(!image.isLoaded()){
            System.err.println("no image loaded");
            System.exit(2);
            return;
        }

        WeightsMap weightsMap = new WeightsMap(image);
        ReplacingMap replacingMap = new ReplacingMap(weightsMap);
        Palette palette = new Palette(weightsMap, image.size());

        image.applyReplacingMap(replacingMap);

        REDUCED_IMAGE.getOptString().ifPresent(image::saveToBMP);

        PALETTE_IMAGE.getOptString().ifPresent(palette::renderPalette);

        M.finishAndShowResult();

    }


}
