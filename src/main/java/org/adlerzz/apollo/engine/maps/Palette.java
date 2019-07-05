package org.adlerzz.apollo.engine.maps;

import org.adlerzz.apollo.engine.singles.HSV;
import org.adlerzz.apollo.engine.singles.PaletteItem;
import org.adlerzz.apollo.engine.utils.HSVUtils;
import org.adlerzz.apollo.engine.utils.RenderUtils;
import org.adlerzz.apollo.app.measuretime.MeasureTime;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static org.adlerzz.apollo.app.param.Param.CUT_OFF_THRESHOLD;
import static org.adlerzz.apollo.app.param.Param.PALETTE_FORMAT;
import static org.adlerzz.apollo.engine.utils.Constants.*;

@Component
public class Palette {
    private LinkedList<PaletteItem> palette;
    private static final HSVUtils HSV_UTILS = HSVUtils.getInstance();
    private static final RenderUtils RENDER_UTILS = RenderUtils.getInstance();

    public Palette() {
        this.palette = new LinkedList<>();
    }

    @MeasureTime
    public void makePalette(WeightsMap weightsMap){
        this.palette.clear();
        for (Map.Entry<HSV, Map<HSV, Integer>> weightEl : weightsMap.getWeightsMap().entrySet()) {
            HSV normalized = HSV_UTILS.normalize(weightEl.getValue());
            int weight = HSV_UTILS.weight(weightEl.getValue());

            this.palette.add(new PaletteItem(normalized, weight));
        }

        this.palette.sort(Comparator.comparingLong(PaletteItem::getCount).reversed());
    }

    @MeasureTime
    public void cutoffRare(int size) {
        double th = CUT_OFF_THRESHOLD.getValue();

        int threshold = (int) (size * th);
        int mainAmount = 0;
        for (PaletteItem i : this.palette) {
            threshold -= i.getCount();
            mainAmount++;
            if (threshold < 0) {
                break;
            }
        }

        List<PaletteItem> limited = this.palette.stream().limit(mainAmount).collect(Collectors.toList());

        int all = limited.stream().map(PaletteItem::getCount).reduce(0, Integer::sum);
        int p_threshold = all / 360;
        limited = limited.stream().filter(p -> p.getCount() > p_threshold).collect(Collectors.toList());

        this.palette.clear();
        this.palette.addAll(limited);

    }

    @MeasureTime
    public void rearrange() {

        final List<PaletteItem> blackened = new ArrayList<>();
        final List<PaletteItem> grayened = new ArrayList<>();
        final List<PaletteItem> colorful = new ArrayList<>();

        this.palette.forEach(p -> {
            if (p.getColor().getV() < BLACKENED_V_THRESHOLD) {
                blackened.add(p);
            } else if (p.getColor().getV() >= GRAYENED_V_THRESHOLD && p.getColor().getS() < GRAYENED_S_THRESHOLD) {
                grayened.add(p);
            } else {
                colorful.add(p);
            }

        });

        blackened.sort(Comparator.comparingInt(o -> o.getColor().getV()));
        colorful.sort(Comparator.comparingInt(o -> o.getColor().getH()));
        grayened.sort(Comparator.comparingInt(o -> o.getColor().getV()));

        this.palette.clear();
        this.palette.addAll(blackened);
        this.palette.addAll(colorful);
        this.palette.addAll(grayened);

    }

    @MeasureTime
    public void renderPalette(String fileName) {

        final int tileRowCount = (int) Math.ceil(Math.sqrt(this.palette.size()));

        try (OutputStream os = new FileOutputStream(fileName)) {

            BufferedImage bi = new BufferedImage(tileRowCount * TILE_SIZE, tileRowCount * TILE_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bi.createGraphics();

            Iterator<PaletteItem> i = this.palette.listIterator();
            for (int ry = 0; ry < tileRowCount; ry++) {
                for (int rx = 0; rx < tileRowCount; rx++) {

                    Paint paint;
                    if(i.hasNext()){
                        paint = HSV_UTILS.toColor(i.next().getColor());
                    } else {
                        paint = RENDER_UTILS.getTransparentTile();
                    }

                    g2d.setPaint(paint);
                    g2d.fillRect(rx * TILE_SIZE, ry * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
            g2d.dispose();

            if (PALETTE_FORMAT.getOptional().isPresent()) {
                ImageIO.write(bi, PALETTE_FORMAT.getValue(), os);
            }
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    @MeasureTime
    public void renderPieDiagram(String fileName) {

        final Rectangle2D box = new Rectangle2D.Double(0, 0, PIE_WIDTH, PIE_HEIGHT);
        final Ellipse2D outerRegion = new Ellipse2D.Double(PIE_PADDING, PIE_PADDING, PIE_WIDTH - 2 * PIE_PADDING, PIE_HEIGHT - 2 * PIE_PADDING);
        final Ellipse2D innerRegion = new Ellipse2D.Double(
                PIE_PADDING + PIE_RING_WIDTH,
                PIE_PADDING + PIE_RING_WIDTH,
                PIE_WIDTH - 2 * (PIE_PADDING + PIE_RING_WIDTH),
                PIE_HEIGHT - 2 * (PIE_PADDING + PIE_RING_WIDTH)
        );

        int all = this.palette.stream().map(PaletteItem::getCount).reduce(0, Integer::sum);
        try (OutputStream os = new FileOutputStream(fileName)) {

            BufferedImage bi = new BufferedImage(PIE_WIDTH, PIE_HEIGHT, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = bi.createGraphics();
            g2d.setPaint(RENDER_UTILS.getTransparentTile());
            g2d.fill(box);

            double angle = 0;
            for (PaletteItem item : this.palette) {
                double offset = item.getCount() * 360.0 / all;

                Color color = HSV_UTILS.toColor(item.getColor());

                g2d.setPaint(color);
                g2d.fill(new Arc2D.Double(outerRegion.getBounds(), angle, offset, Arc2D.PIE));

                angle += offset;
            }

            g2d.setPaint(RENDER_UTILS.getTransparentTile());
            g2d.fill(innerRegion);

            g2d.dispose();

            if (PALETTE_FORMAT.getOptional().isPresent()) {
                ImageIO.write(bi, PALETTE_FORMAT.getValue(), os);
            }

        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
}
