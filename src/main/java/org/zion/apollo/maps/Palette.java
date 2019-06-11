package org.zion.apollo.maps;

import org.zion.apollo.data.HSV;
import org.zion.apollo.data.PaletteItem;
import org.zion.apollo.data.RGBA;
import org.zion.apollo.utils.HSVUtilities;
import org.zion.apollo.utils.TimeMeasurements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.zion.apollo.utils.Constants.*;

public class Palette {
    private LinkedList<PaletteItem> palette;
    private static final HSVUtilities HSV_UTILITIES = HSVUtilities.getInstance();
    private final TimeMeasurements TM;


    public Palette(WeightsMap weightsMap, int size /*temporary*/) {
        TM = new TimeMeasurements();
        TM.start(" Creating palette... ");
        this.palette = new LinkedList<>();

        for(Map.Entry<HSV, Map<HSV, Integer>> weightEl: weightsMap.getWeightsMap().entrySet()) {
            HSV normalized = HSV_UTILITIES.normalize(weightEl.getValue());
            int weight = HSV_UTILITIES.weight(weightEl.getValue());

            this.palette.add( new PaletteItem(normalized, weight) );
        }
        TM.finishAndShowResult();

        this.palette.sort(Comparator.comparingLong( (PaletteItem a) -> a.count).reversed());
        this.cutoffRare(size);
        this.rearrange();
    }

    private void cutoffRare(int size){
        TM.start("  Cutting off rare colors... ");
        int threshold = (int)( size * MAIN_PART);
        int i = 0;
        while(threshold >= 0 && i < this.palette.size()){
            threshold -= this.palette.get(i).count;
            i++;
        }
        int mainAmount = i;
        List<PaletteItem> limited = this.palette
                .stream()
                .limit(mainAmount)
                .collect(Collectors.toList());
        this.palette.clear();
        this.palette.addAll(limited);
        TM.finishAndShowResult();
    }

    private void rearrange(){
        TM.start("  Rearrange tiles... ");

        final List<PaletteItem> blackened = new ArrayList<>();
        final List<PaletteItem> grayened = new ArrayList<>();
        final List<PaletteItem> colorful = new ArrayList<>();
        this.palette.forEach( p -> {
            if(p.color.getV() < BLACKENED_V_THRESHOLD){
                blackened.add(p);
            } else if(p.color.getV() >= GRAYENED_V_THRESHOLD && p.color.getS() < GRAYENED_S_THRESHOLD) {
                grayened.add(p);
            } else {
                colorful.add(p);
            }
        });

        blackened.sort(Comparator.comparingInt( o -> o.color.getV() ));
        colorful.sort(Comparator.comparingInt( o -> o.color.getH() ));
        grayened.sort(Comparator.comparingInt( o -> o.color.getV() ));

        this.palette.clear();
        this.palette.addAll(blackened);
        this.palette.addAll(colorful);
        this.palette.addAll(grayened);
        TM.finishAndShowResult();
    }

    public void renderPalette(String fileName) {
        TM.start(" Save palette to \"" + fileName + "\"... ");


        final int tileCountX = (int)Math.ceil( Math.sqrt(this.palette.size()*1.0));
        final int tileCountY = (int)Math.ceil( Math.sqrt(this.palette.size()*1.0));

        try(OutputStream os = new FileOutputStream(fileName)) {

            BufferedImage bi = new BufferedImage(tileCountX * tileSize, tileCountY * tileSize, BufferedImage.TYPE_INT_RGB);
            for (int ry = 0; ry < tileCountY; ry++) {
                for (int rx = 0; rx < tileCountX; rx++) {
                    int k = rx + ry * tileCountX;

                    if (k < this.palette.size()) {
                        int color = (new RGBA(this.palette.get(k).color)).toRaw();

                        for (int y = 0; y < tileSize; y++) {
                            for (int x = 0; x < tileSize; x++) {
                                bi.setRGB(x + rx * tileSize, y + ry * tileSize, color);
                            }
                        }

                    } else {

                        for (int y = 0; y < tileSize; y++) {
                            for (int x = 0; x < tileSize; x++) {
                                if (((x * transparentTileSize / tileSize + y * transparentTileSize / tileSize) % 2) == 0) {
                                    bi.setRGB(x + rx * tileSize, y + ry * tileSize, transparentColor1);
                                } else {
                                    bi.setRGB(x + rx * tileSize, y + ry * tileSize, transparentColor2);
                                }
                            }
                        }
                    }


                }
            }
            ImageIO.write(bi, FORMAT_PNG, os);
            TM.finishAndShowResult();
        } catch (IOException ex){
            System.err.println(ex.getLocalizedMessage());
        }
    }

}
