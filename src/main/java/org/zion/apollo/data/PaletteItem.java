package org.zion.apollo.data;

public class PaletteItem {
    public HSV color;
    public int count;


    public PaletteItem ( HSV color, int count ) {
        this.color = new HSV(color);
        this.count = count;
    }

    public String toString()
    {
        return String.format ( "PaletteItem{color: %s, count:%d}",
                this.color.toString (),
                this.count);
    }
}
