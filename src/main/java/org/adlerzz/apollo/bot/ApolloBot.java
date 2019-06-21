package org.adlerzz.apollo.bot;

import com.google.common.collect.Iterables;
import org.adlerzz.apollo.calc.maps.Image;
import org.adlerzz.apollo.calc.maps.Palette;
import org.adlerzz.apollo.calc.maps.WeightsMap;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.adlerzz.apollo.app.Param.PALETTE_FORMAT;

@Service
public class ApolloBot extends TelegramLongPollingBot {

    public ApolloBot(){
        System.out.println("creating bot");
    }

    @Override
    public void onUpdateReceived(Update update) {
        PhotoSize image = Iterables.getLast(update.getMessage().getPhoto());
        GetFile getFile = new GetFile().setFileId(image.getFileId());
        try{
            File file = execute(getFile);
            String filename = downloadFile(file).getAbsolutePath();
            System.out.println(filename);
            Image img = new Image();
            img.loadFromBMP(filename);

            WeightsMap weightsMap = new WeightsMap(img);
            Palette palette = new Palette(weightsMap, img.size());

            String paletteFile = filename + "_palette." + PALETTE_FORMAT.getValue();
            palette.renderPalette(paletteFile);


            SendPhoto sender = new SendPhoto()
                    .setChatId(update.getMessage().getChatId())
                    .setPhoto( "photo", new FileInputStream(paletteFile) );
            execute(sender);
            java.io.File infile = new java.io.File(filename);
            infile.delete();
            java.io.File outfile = new java.io.File(paletteFile);
            outfile.delete();
        } catch (TelegramApiRequestException e) {
            System.err.println( e.getApiResponse() );
            e.printStackTrace();
        } catch (TelegramApiException e){
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e){
            System.err.println( e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return "ApolloBot";
    }

    @Override
    public String getBotToken() {
        return "624923142:AAGha-1rrk4_BLo6_dGrdJOWqZ_YK8ajB3c";
    }
}
