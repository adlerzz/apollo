package org.zion.apollo.maps;

import org.zion.apollo.data.HSV;
import org.zion.apollo.data.PaletteItem;
import org.zion.apollo.utils.HSVUtils;
import org.zion.apollo.utils.RenderUtils;
import org.zion.apollo.utils.TimeMeasurements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.zion.apollo.utils.Constants.*;
import static org.zion.app.config.Param.CUT_OFF_THRESHOLD;
import static org.zion.app.config.Param.PALETTE_FORMAT;

public class Palette {
    private LinkedList<PaletteItem> palette;
    private static final HSVUtils HSV_UTILS = HSVUtils.getInstance();
    private static final RenderUtils RENDER_UTILS = RenderUtils.getInstance();
    private final TimeMeasurements TM;


    public Palette(WeightsMap weightsMap, int size /*temporary*/) {
        TM = new TimeMeasurements();
        TM.start(" Creating palette... ");
        this.palette = new LinkedList<>();

        for (Map.Entry<HSV, Map<HSV, Integer>> weightEl : weightsMap.getWeightsMap().entrySet()) {
            HSV normalized = HSV_UTILS.normalize(weightEl.getValue());
            int weight = HSV_UTILS.weight(weightEl.getValue());

            this.palette.add(new PaletteItem(normalized, weight));
        }
        TM.finishAndShowResult();

        this.palette.sort(Comparator.comparingLong(PaletteItem::getCount).reversed());
        this.cutoffRare(size);
        this.rearrange();
    }

    private void cutoffRare(int size) {
        double th = (Double)CUT_OFF_THRESHOLD.getValue();
        TM.start("  Cutting off rare colors... Threshold: " + th*100 + "%. ");

        int threshold = (int) (size * th);
        int i = 0;
        while (threshold >= 0 && i < this.palette.size()) {
            threshold -= this.palette.get(i).getCount();
            i++;
        }
        int mainAmount = i;
        List<PaletteItem> limited = this.palette
                .stream()
                .limit(mainAmount)
                .collect(Collectors.toList());

        int all = limited.stream().map(PaletteItem::getCount).reduce(0, Integer::sum);
        int p_threshold = all / 360;
        limited = limited.stream().filter( p -> p.getCount() > p_threshold ).collect(Collectors.toList());

        this.palette.clear();
        this.palette.addAll(limited);


        TM.finishAndShowResult();
    }

    private void rearrange() {
        TM.start("  Rearrange tiles... ");

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
        TM.finishAndShowResult();
    }

    public void renderPalette(String fileName) {
        TM.start(" Save palette to \"" + fileName + "\"... ");

        final int tileRowCount = (int) Math.ceil(Math.sqrt(this.palette.size()));


        try (OutputStream os = new FileOutputStream(fileName)) {

            BufferedImage bi = new BufferedImage(tileRowCount * TILE_SIZE, tileRowCount * TILE_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bi.createGraphics();

            int k = 0;

            for (int ry = 0; ry < tileRowCount; ry++) {
                for (int rx = 0; rx < tileRowCount; rx++) {

                    Paint paint;
                    if (k < this.palette.size()) {
                        paint = HSV_UTILS.toColor(this.palette.get(k).getColor());
                    } else {
                        paint = RENDER_UTILS.getTransparentTile();
                    }
                    g2d.setPaint(paint);
                    g2d.fillRect( rx* TILE_SIZE, ry* TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    k++;
                }
            }
            g2d.dispose();

            if(PALETTE_FORMAT.getOptString().isPresent()) {
                ImageIO.write(bi, PALETTE_FORMAT.getOptString().get(), os);
            }
            TM.finishAndShowResult();
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    public void renderPieDiagram(String fileName) {
        TM.start(" Save pie diagram to \"" + fileName + "\"... ");

        final Rectangle2D box = new Rectangle2D.Double(0, 0, PIE_WIDTH, PIE_HEIGHT);
        final Ellipse2D outerRegion = new Ellipse2D.Double(PIE_PADDING, PIE_PADDING, PIE_WIDTH - 2*PIE_PADDING, PIE_HEIGHT - 2*PIE_PADDING);
        final Ellipse2D innerRegion = new Ellipse2D.Double(
            PIE_PADDING + PIE_RING_WIDTH,
            PIE_PADDING + PIE_RING_WIDTH,
            PIE_WIDTH - 2*(PIE_PADDING + PIE_RING_WIDTH),
            PIE_HEIGHT - 2*(PIE_PADDING + PIE_RING_WIDTH)
        );

        int all = this.palette.stream().map(PaletteItem::getCount).reduce(0, Integer::sum);
        try (OutputStream os = new FileOutputStream(fileName)) {

            BufferedImage bi = new BufferedImage(PIE_WIDTH, PIE_HEIGHT, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = bi.createGraphics();
            g2d.setPaint(RENDER_UTILS.getTransparentTile());
            g2d.fill( box );

            double angle = 0;
            for(PaletteItem item: this.palette){
                double offset = item.getCount() * 360.0 / all;

                Color color = HSV_UTILS.toColor(item.getColor());

                g2d.setPaint( color );
                g2d.fill(new Arc2D.Double(outerRegion.getBounds(), angle, offset, Arc2D.PIE));

                angle += offset;
            }

            g2d.setPaint( RENDER_UTILS.getTransparentTile() );
            g2d.fill( innerRegion );

            g2d.dispose();

            if(PALETTE_FORMAT.getOptString().isPresent()) {
                ImageIO.write(bi, PALETTE_FORMAT.getOptString().get(), os);
            }
            TM.finishAndShowResult();

        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
}
