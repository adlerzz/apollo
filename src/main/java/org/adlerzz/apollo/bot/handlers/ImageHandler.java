package org.adlerzz.apollo.bot.handlers;

import com.google.common.collect.Iterables;
import org.adlerzz.apollo.bot.ApolloBot;
import org.adlerzz.apollo.engine.maps.Image;
import org.adlerzz.apollo.engine.maps.Palette;
import org.adlerzz.apollo.engine.maps.ReplacingMap;
import org.adlerzz.apollo.engine.maps.WeightsMap;
import org.adlerzz.apollo.app.measuretime.MeasureTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.FileInputStream;

import static org.adlerzz.apollo.app.param.Param.PALETTE_FORMAT;

@Service
public class ImageHandler extends AbstractHandler{
    private static final Logger log = LoggerFactory.getLogger(ApolloBot.class);

    private Image image;
    private WeightsMap weightsMap;
    private ReplacingMap replacingMap;
    private Palette palette;

    public ImageHandler() {
        log.debug("handler created, {}", this);
    }

    @MeasureTime
    @Override
    public void accept(Message message) {
        PhotoSize image = Iterables.getLast(message.getPhoto());
        GetFile getFile = new GetFile().setFileId(image.getFileId());
        try{
            File file = this.getBot().execute(getFile);
            String filename = this.getBot().downloadFile(file).getAbsolutePath();
            log.debug("download to {}", filename);

            this.image.loadFromFile(filename);

            weightsMap.makeMap(this.image);

            palette.makePalette(weightsMap);
            palette.cutoffRare(this.image.getSize());
            palette.rearrange();

            String paletteFormat = PALETTE_FORMAT.getValue();
            String paletteFile = filename + "_palette." + paletteFormat;
            palette.renderPalette(paletteFile);

            SendPhoto sender = new SendPhoto()
                    .setChatId(message.getChatId())
                    .setPhoto( "photo", new FileInputStream(paletteFile) );
            this.getBot().execute(sender);

            if( !cleanup(filename, paletteFile)){
                log.warn("Temporary files weren't deleted");
            }

        } catch (Exception e){
            log.error("Exception thrown: ", e);
            //e.printStackTrace();

        }
    }

    private boolean cleanup(String... files){
        boolean result = true;
        for(String file: files){
            if( !(new java.io.File(file)).delete()){
                result = false;
            }
        }
        return result;
    }

    @Autowired
    public void setImage(Image image) {
        this.image = image;
    }

    @Autowired
    public void setWeightsMap(WeightsMap weightsMap) {
        this.weightsMap = weightsMap;
    }

    @Autowired
    public void setReplacingMap(ReplacingMap replacingMap) {
        this.replacingMap = replacingMap;
    }

    @Autowired
    public void setPalette(Palette palette) {
        this.palette = palette;
    }

}
