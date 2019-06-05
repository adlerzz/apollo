package org.zion.apollo.maps;

import org.zion.apollo.data.HSV;
import org.zion.apollo.data.PaletteItem;
import org.zion.apollo.data.RGBA;
import org.zion.apollo.utils.HSVUtilities;

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
    //private static final int tileCountX = 15;
    private static final int tileSize = 40;
    private static final int transparentTileSize = 8;
    private static final int transparentColor1 = 0xF0F0F0;
    private static final int transparentColor2 = 0xA0A0A0;

    public Palette(WeightsMap weightsMap, int size /*temporary*/) {
        long startTime = (new Date()).getTime();
        this.palette = new LinkedList<>();
        System.out.print("Creating palette...");

        for(Map.Entry<HSV, Map<HSV, Integer>> weightEl: weightsMap.getWeightsMap().entrySet()) {
            HSV normalized = HSV_UTILITIES.normalize(weightEl.getValue());
            int weight = HSV_UTILITIES.weight(weightEl.getValue());

            this.palette.add( new PaletteItem(normalized, weight) );
        }
        long endTime = (new Date()).getTime();
        System.out.println(" Done in " + (endTime - startTime) + " ms");

        this.palette.sort(Comparator.comparingLong( (PaletteItem a) -> a.count).reversed());
        this.cutoffRare(size);
        this.rearrange();
    }

    private void cutoffRare(int size){
        System.out.print("Cutting off rare colors ...");
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
        System.out.println(" Done.");
    }

    private void rearrange(){
        long startTime = (new Date()).getTime();
        System.out.print("Rearrange tiles...");

        List<PaletteItem> blackened = this.palette.stream()
                .filter( p -> p.color.getV() < (short)0x28)
                .sorted( Comparator.comparingInt( o -> o.color.getV() ) )
                .collect(Collectors.toList());

        List<PaletteItem> whitened = this.palette.stream()
                .filter( p -> p.color.getV() >= (short)0xA0 && p.color.getS() < (short)0x30)
                .sorted( Comparator.comparingInt( o -> o.color.getV() ) )
                .collect(Collectors.toList());

        List<PaletteItem> colorful = this.palette.stream()
                .filter( p -> p.color.getV() >= (short)0x28 )
                .filter( p -> p.color.getV() < (short)0xA0 || p.color.getS() >= (short)0x30 )
                .sorted( Comparator.comparingInt( o -> o.color.getH() ) )
                .collect(Collectors.toList());

        this.palette.clear();
        this.palette.addAll(blackened);
        this.palette.addAll(colorful);
        this.palette.addAll(whitened);
        long endTime = (new Date()).getTime();
        System.out.println(" Done in " + (endTime - startTime) + " ms");
    }

    public void renderPalette(String fileName) {
        long startTime = (new Date()).getTime();

        System.out.print("Save palette to \"" + fileName + "\"...");

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
            long endTime = (new Date()).getTime();
            System.out.println(" Done in " + (endTime - startTime) + " ms");
        } catch (IOException ex){
            System.err.println(ex.getLocalizedMessage());
        }
    }


    public void showPalette(int size /* temporary*/) {
        ListIterator<PaletteItem> i = this.palette.listIterator();
        while( i.hasNext()){
            int index = i.nextIndex();
            PaletteItem item = i.next();
            double percent =  item.count*100.0/(size * MAIN_PART);
            // double percent =  item.count*100.0/(this.size());
            System.out.printf("(%03d) %s  \t| %6.3f%%%n", index, item, percent);
        }

    }
}
