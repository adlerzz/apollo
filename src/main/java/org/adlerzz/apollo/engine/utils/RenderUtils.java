package org.adlerzz.apollo.engine.utils;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static org.adlerzz.apollo.engine.utils.Constants.*;

@Component
public class RenderUtils {
    private static TexturePaint TRANSPARENT_TEXTURE_INSTANCE = null;

    public RenderUtils(){}

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
