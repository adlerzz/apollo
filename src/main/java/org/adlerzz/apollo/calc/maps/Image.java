package org.adlerzz.apollo.calc.maps;

import org.adlerzz.apollo.calc.singles.HSV;
import org.adlerzz.apollo.calc.singles.RGBA;
import org.adlerzz.apollo.calc.utils.TimeMeasurements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.adlerzz.apollo.app.Param.PALETTE_FORMAT;

public class Image {

    private final TimeMeasurements TM;

    private ArrayList<HSV> hsvMap;
    private boolean loaded;

    private int width;
    private int height;

    public Image() {
        this.hsvMap = new ArrayList<>();
        this.loaded = false;
        this.TM = new TimeMeasurements();
    }

    public void loadFromBMP(String fileName) {
        TM.start(" Loading from file \"" + fileName + "\"... ");

        try(InputStream is = new FileInputStream(fileName)) {
            BufferedImage bi = ImageIO.read(is);
            this.width = bi.getWidth();
            this.height = bi.getHeight();

            int[] map = bi.getRGB(0, 0, this.width, this.height, null, 0, this.width);

            this.hsvMap = Arrays.stream(map).parallel().mapToObj(RGBA::new).map(HSV::new).collect(Collectors.toCollection(ArrayList::new));

            this.loaded = true;
            TM.finishAndShowResult();

        } catch (IOException ex){
            this.loaded = false;
            System.err.println(ex.getLocalizedMessage());
        }
    }

    public void saveToBMP(String fileName) {
        TM.start(" Saving to file \"" + fileName + "\"... ");
        try(OutputStream os = new FileOutputStream(fileName)) {
            BufferedImage bi = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            int k = 0;
            for (int j = 0; j < this.height; j++) {
                for (int i = 0; i < this.width; i++) {
                    bi.setRGB(i, j, (new RGBA(this.hsvMap.get(k))).toRaw());
                    k++;
                }
            }
            if( PALETTE_FORMAT.getOptString().isPresent()) {
                ImageIO.write(bi, PALETTE_FORMAT.getOptString().get(), os);
            }
            TM.finishAndShowResult();
        } catch (IOException ex){
            System.err.println(ex.getLocalizedMessage());
        }
    }

    public ArrayList<HSV> getHSVMap(){
        return this.hsvMap;
    }

    public boolean isLoaded(){
        return this.loaded;
    }

    public void applyReplacingMap(ReplacingMap replacingMap){
        TM.start(" Start applying of the replacing map... ");

        this.hsvMap.parallelStream().forEach( p -> p.setAs(replacingMap.getReducingReplacing(p)) );

        TM.finishAndShowResult();
    }

    public int size() {
        return this.width * this.height;
    }
}
