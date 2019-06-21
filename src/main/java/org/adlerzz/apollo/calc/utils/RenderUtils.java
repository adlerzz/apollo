package org.adlerzz.apollo.calc.utils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static org.adlerzz.apollo.calc.utils.Constants.*;


public class RenderUtils {
    private static RenderUtils INSTANCE = null;
    private static TexturePaint TRANSPARENT_TEXTURE_INSTANCE = null;

    private RenderUtils(){}

    public static RenderUtils getInstance(){
        if(INSTANCE == null){
            INSTANCE = new RenderUtils();
        }
        return INSTANCE;
    }

    public TexturePaint getTransparentTile(){
        if(TRANSPARENT_TEXTURE_INSTANCE == null ) {
            BufferedImage transparentTile = new BufferedImage(TRANSPARENT_TILE_SIZE, TRANSPARENT_TILE_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D transparentTileG2D = transparentTile.createGraphics();
            transparentTileG2D.setPaint( new Color(TRANSPARENT_COLOR_1) );
            transparentTileG2D.fillRect(0, 0, TRANSPARENT_TILE_SIZE /2, TRANSPARENT_TILE_SIZE /2);
            transparentTileG2D.fillRect(TRANSPARENT_TILE_SIZE /2, TRANSPARENT_TILE_SIZE /2, TRANSPARENT_TILE_SIZE, TRANSPARENT_TILE_SIZE);
            transparentTileG2D.setPaint( new Color(TRANSPARENT_COLOR_2) );
            transparentTileG2D.fillRect(0, TRANSPARENT_TILE_SIZE /2, TRANSPARENT_TILE_SIZE /2, TRANSPARENT_TILE_SIZE);
            transparentTileG2D.fillRect(TRANSPARENT_TILE_SIZE /2, 0, TRANSPARENT_TILE_SIZE, TRANSPARENT_TILE_SIZE /2);
            transparentTileG2D.dispose();

            TRANSPARENT_TEXTURE_INSTANCE = new TexturePaint(transparentTile, new Rectangle2D.Double(0,0, TRANSPARENT_TILE_SIZE, TRANSPARENT_TILE_SIZE));
        }

        return TRANSPARENT_TEXTURE_INSTANCE;
    }

    public void pave(){

    }
}
