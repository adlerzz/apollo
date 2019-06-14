package org.zion.apollo.data;

public class PaletteItem {
    private HSV color;
    private int count;


    public PaletteItem ( HSV color, int count ) {
        this.color = new HSV(color);
        this.count = count;
    }

    public int getCount(){
        return this.count;
    }

    public HSV getColor(){
        return this.color;
    }

    public String toString() {
        return String.format ( "PaletteItem{color: %s, count:%d}",
                this.color.toString (),
                this.count);
    }
}
