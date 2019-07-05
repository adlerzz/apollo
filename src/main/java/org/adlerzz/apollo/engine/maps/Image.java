package org.adlerzz.apollo.engine.maps;

import org.adlerzz.apollo.engine.singles.HSV;
import org.adlerzz.apollo.engine.singles.RGBA;
import org.adlerzz.apollo.app.measuretime.MeasureTime;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.adlerzz.apollo.app.param.Param.PALETTE_FORMAT;

@Component
public class Image {

    private ArrayList<HSV> hsvMap;
    private boolean loaded;

    private int width;
    private int height;

    public Image() {
        this.hsvMap = new ArrayList<>();
        this.loaded = false;

    }

    @MeasureTime
    public void loadFromFile(String fileName) {


        try(InputStream is = new FileInputStream(fileName)) {
            BufferedImage bi = ImageIO.read(is);
            this.width = bi.getWidth();
            this.height = bi.getHeight();

            int[] map = bi.getRGB(0, 0, this.width, this.height, null, 0, this.width);

            this.hsvMap = Arrays.stream(map)
                    .parallel()
                    .mapToObj(RGBA::new)
                    .map(HSV::new)
                    .collect(Collectors.toCollection(ArrayList::new));

            this.loaded = true;

        } catch (IOException ex){
            this.loaded = false;
            System.err.println(ex.getLocalizedMessage());
        }
    }

    @MeasureTime
    public void saveToFile(String fileName) {
        try(OutputStream os = new FileOutputStream(fileName)) {
            BufferedImage bi = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            int k = 0;
            for (int j = 0; j < this.height; j++) {
                for (int i = 0; i < this.width; i++) {
                    bi.setRGB(i, j, (new RGBA(this.hsvMap.get(k))).toRaw());
                    k++;
                }
            }
            if( PALETTE_FORMAT.getOptional().isPresent()) {
                ImageIO.write(bi, PALETTE_FORMAT.getValue(), os);
            }
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

    @MeasureTime
    public void applyReplacingMap(ReplacingMap replacingMap){
        this.hsvMap.parallelStream().forEach( p -> p.setAs(replacingMap.getReducingReplacing(p)) );
    }

    public int getSize() {
        return this.width * this.height;
    }
}
