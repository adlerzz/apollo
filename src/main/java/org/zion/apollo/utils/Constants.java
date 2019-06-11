package org.zion.apollo.utils;

/**
 *
 * @author adlerzz
 */
public interface Constants
{
    byte HEXOYA = 42;

    double MAIN_PART = 0.95;
    String FORMAT_BMP = "bmp";
    String FORMAT_PNG = "png";

    short H_REDUCE_MASK = 0xE0;
    short S_REDUCE_MASK = 0xE0;
    short V_REDUCE_MASK = 0xE0;

    short BLACKENED_V_THRESHOLD = 0x40;
    short GRAYENED_V_THRESHOLD = 0xA0;
    short GRAYENED_S_THRESHOLD = 0x40;

    int tileSize = 60;
    int transparentTileSize = 6;
    int transparentColor1 = 0xF0F0F0;
    int transparentColor2 = 0xA0A0A0;
}
