package org.adlerzz.apollo.engine.utils;

/**
 *
 * @author adlerzz
 */
public interface Constants
{
    byte HEXOYA = 42;

    short H_REDUCE_MASK = 0xE0;
    short S_REDUCE_MASK = 0xE0;
    short V_REDUCE_MASK = 0xE0;

    short BLACKENED_V_THRESHOLD = 0x40;
    short GRAYENED_V_THRESHOLD = 0xA0;
    short GRAYENED_S_THRESHOLD = 0x40;

    int TILE_SIZE = 60;
    int TRANSPARENT_TILE_SIZE = 20;
    int TRANSPARENT_COLOR_1 = 0xF0F0F0;
    int TRANSPARENT_COLOR_2 = 0xC0C0C0;

    int PIE_WIDTH = 1000;
    int PIE_HEIGHT = 1000;
    int PIE_PADDING = 100;
    int PIE_RING_WIDTH = 150;
}
