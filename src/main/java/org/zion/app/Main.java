package org.zion.app;

import org.zion.apollo.maps.Image;
import org.zion.apollo.maps.Palette;
import org.zion.apollo.maps.ReplacingMap;
import org.zion.apollo.maps.WeightsMap;
import org.zion.app.config.ParsedProgArgs;

import java.util.Date;

import static org.zion.app.config.Param.*;

public class Main {
    private static final ParsedProgArgs ARGS = ParsedProgArgs.getInstance();

    public static void main(String[] args) {

        long startTime = (new Date()).getTime();
        ARGS.parse(args);

        if(ARGS.getStringParamOrNull(INPUT_IMAGE) == null){
            System.err.println("no input file");
            System.exit(1);
            return;
        }
        String fullFileName = ARGS.getStringParamOrNull(INPUT_IMAGE);
        String fileName = fullFileName.substring(0, fullFileName.lastIndexOf("."));

        ARGS.fillIfEmptyParam(PALETTE_FORMAT, "bmp");

        ARGS.fillIfEmptyParam(PALETTE_IMAGE, fileName + "_palette." + ARGS.getStringParamOrNull(PALETTE_FORMAT));


        Image image = new Image();
        ARGS.getStringParam( INPUT_IMAGE ).ifPresent( image::loadFromBMP );

        if(!image.isLoaded()){
            System.err.println("no image loaded");
            System.exit(2);
            return;
        }

        WeightsMap weightsMap = new WeightsMap(image);
        ReplacingMap replacingMap = new ReplacingMap(weightsMap);
        Palette palette = new Palette(weightsMap, image.size());

        image.applyReplacingMap(replacingMap);

        ARGS.getStringParam( REDUCED_IMAGE ).ifPresent( image::saveToBMP );

        ARGS.getStringParam( PALETTE_IMAGE ).ifPresent( palette::renderPalette);

        long endTime = (new Date()).getTime();

        System.out.println("Execution time: " + (endTime - startTime) + " ms");

    }


}
