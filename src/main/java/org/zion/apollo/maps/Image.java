package org.zion.apollo.maps;

import org.zion.apollo.data.HSV;
import org.zion.apollo.data.RGBA;

import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import static org.zion.apollo.utils.Constants.FORMAT_BMP;

public class Image {

    private ArrayList<HSV> hsvMap;
    private boolean loaded;

    private int width;
    private int height;

    public Image() {
        this.hsvMap = new ArrayList<>();
        this.loaded = false;
    }

    public void loadFromBMP(String fileName) {
        long startTime = (new Date()).getTime();
        System.out.print("Loading from file \"" + fileName + "\"...");
        try(InputStream is = new FileInputStream(fileName)) {
            BufferedImage bi = ImageIO.read(is);
            this.width = bi.getWidth();
            this.height = bi.getHeight();

            int[] map = bi.getRGB(0, 0, this.width, this.height, null, 0, this.width);

            this.hsvMap = Arrays.stream(map).parallel().mapToObj(RGBA::new).map(HSV::new).collect(Collectors.toCollection(ArrayList::new));

            this.loaded = true;
            long endTime = (new Date()).getTime();
            System.out.println(" Done in " + (endTime - startTime) + " ms");

        } catch (IOException ex){
            this.loaded = false;
            System.err.println(ex.getLocalizedMessage());
        }
    }

    public void saveToBMP(String fileName) {
        long startTime = (new Date()).getTime();
        System.out.print("Saving to file \"" + fileName + "\"...");
        try(OutputStream os = new FileOutputStream(fileName)) {
            BufferedImage bi = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            int k = 0;
            for (int j = 0; j < this.height; j++) {
                for (int i = 0; i < this.width; i++) {
                    bi.setRGB(i, j, (new RGBA(this.hsvMap.get(k))).toRaw());
                    k++;
                }
            }
            ImageIO.write(bi, FORMAT_BMP, os);
            long endTime = (new Date()).getTime();
            System.out.println(" Done in " + (endTime - startTime) + " ms");
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
        long startTime = (new Date()).getTime();
        System.out.print("Start applying of the replacing map...");

        this.hsvMap.parallelStream().forEach( p -> p.setAs(replacingMap.getReducingReplacing(p)) );

        long endTime = (new Date()).getTime();
        System.out.println(" Done in " + (endTime - startTime) + " ms");
    }

    public int size() {
        return this.width * this.height;
    }
}
