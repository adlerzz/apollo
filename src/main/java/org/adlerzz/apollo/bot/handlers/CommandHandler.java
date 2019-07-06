package org.adlerzz.apollo.bot.handlers;

import org.adlerzz.apollo.app.measuretime.MeasureTime;
import org.adlerzz.apollo.app.param.Param;
import org.adlerzz.apollo.bot.ApolloBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CommandHandler extends AbstractHandler{
    private static final Logger log = LoggerFactory.getLogger(ApolloBot.class);

    @MeasureTime
    @Override
    public void accept(Message message) throws TelegramApiException{
        log.debug("text: {}", message.getText());
        switch(message.getText()){
            case "/params": {
                List<String> params = Stream.of(Param.values())
                        .filter( Param::isEditable )
                        .map( param -> (param.name()+ ": " + param.getValue()))
                        .collect(Collectors.toList());
                SendMessage sender = new SendMessage()
                        .setChatId(message.getChatId())
                        .setText( String.join("\n", params) );

                getBot().execute(sender);

            } break;

            case "/setparam": {
                List<List<InlineKeyboardButton>> rows = Stream.of(Param.values())
                        .filter( Param::isEditable )
                        .map( Param::name )
                        .map( name -> new InlineKeyboardButton(name).setCallbackData("sit " + name) )
                        .map(Collections::singletonList)
                        .collect(Collectors.toList());

                InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup().setKeyboard(rows);

                SendMessage sender = new SendMessage()
                        .setChatId(message.getChatId())
                        .setText("Choose parameter")
                        .setReplyMarkup(keyboardMarkup);
                try {
                    getBot().execute(sender);
                } catch (TelegramApiException e) {
                    log.error("exception thrown: ", e);
                }
            } break;

            default: {
                log.debug("receive: {}", message.getText());
            }
        }
    }

    @MeasureTime
    public void react(CallbackQuery callback) throws TelegramApiException{
        String[] cmds = callback.getData().split(" ", 2);
        if(cmds.length == 2 && "sit".equals(cmds[0]) ){
            Param param = Param.valueOf(cmds[1]);
            SendMessage sender = new SendMessage()
                    .setChatId(callback.getMessage().getChatId())
                    .setText( param.name() + ": " + param.getValue().toString() );

             getBot().execute(sender);

        }

    }

}
