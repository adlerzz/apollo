package org.adlerzz.apollo.bot.handlers;

import org.adlerzz.apollo.app.measuretime.MeasureTime;
import org.adlerzz.apollo.app.param.Param;
import org.adlerzz.apollo.bot.ApolloBot;
import org.adlerzz.apollo.bot.utils.KeyboardUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CommandHandler extends AbstractHandler{
    private static final Logger log = LoggerFactory.getLogger(ApolloBot.class);
    private static final String CMD_PARAMS = "/params";
    private static final String CMD_SET_PARAM = "/setparam";

    private KeyboardUtils keyboardUtils;

    private boolean inDialog;
    private Param handledParam;

    public CommandHandler(){
        this.inDialog = false;
        this.handledParam = null;
    }

    @MeasureTime
    @Override
    public void accept(Message message) throws TelegramApiException{
        log.debug("text: {}", message.getText());
        switch(message.getText()){
            case CMD_PARAMS: {
                List<String> params = Stream.of(Param.values())
                        .filter( Param::isEditable )
                        .map( param -> (param.name()+ ": " + param.getValue()))
                        .collect(Collectors.toList());
                SendMessage sender = new SendMessage()
                        .setChatId(message.getChatId())
                        .setText( String.join("\n", params) );
                getBot().execute(sender);
            } break;

            case CMD_SET_PARAM: {
                List<String> captions = Stream
                        .of(Param.values())
                        .filter(Param::isEditable)
                        .map(Param::name)
                        .collect(Collectors.toList());
                ReplyKeyboard keyboardMarkup = keyboardUtils.createInlineKeyboardFromCaptionsList(captions, "sit");
                SendMessage sender = new SendMessage()
                        .setChatId(message.getChatId())
                        .setText("Choose parameter")
                        .setReplyMarkup(keyboardMarkup);
                 getBot().execute(sender);
            } break;

            default: {
                log.debug("receive: {}", message.getText());

                if(this.inDialog){
                    Class paramClass = this.handledParam.getValue().getClass();

                    if( this.handledParam.getValue() instanceof Double){
                        this.handledParam.setValue( Double.parseDouble (message.getText()) );
                    } else if( this.handledParam.getValue() instanceof Boolean){
                        this.handledParam.setValue( Boolean.parseBoolean (message.getText()) );
                    } else {
                        this.handledParam.setValue(paramClass.cast(message.getText()));
                    }
                    this.inDialog = false;
                    this.handledParam = null;
                }
            }
        }
    }

    @MeasureTime
    public void react(CallbackQuery callback) throws TelegramApiException{
        String[] buttonData = callback.getData().split(" ", 2);

        if(buttonData.length == 2){
            String command = buttonData[0];
            String data = buttonData[1];
            switch (command){
                case "sit": {
                    Param param = Param.valueOf(data);

                    SendMessage sender = new SendMessage()
                            .setChatId(callback.getMessage().getChatId())
                            .setText( param.name() + ": " + param.getString() + "\n Enter new value" );
                    if(param.getDomain() != null && !param.getDomain().isEmpty()){
                        List<String> domain = param.getDomain();
                        ReplyKeyboard reply = keyboardUtils.createInlineKeyboardFromCaptionsList(domain, "cav");
                        sender.setReplyMarkup(reply);
                    }
                    getBot().execute(sender);

                    this.inDialog = true;
                    this.handledParam = param;
                } break;

                case "cav": {
                    log.debug("cav");
                    if(this.inDialog){
                        Class paramClass = this.handledParam.getValue().getClass();

                        if( this.handledParam.getValue() instanceof Double){
                            this.handledParam.setValue( Double.parseDouble (data) );
                        } else if( this.handledParam.getValue() instanceof Boolean){
                            this.handledParam.setValue( Boolean.parseBoolean (data) );
                        } else {
                            this.handledParam.setValue(paramClass.cast(data));
                        }
                        this.inDialog = false;
                        this.handledParam = null;
                        SendMessage sender = new SendMessage()
                                .setChatId(callback.getMessage().getChatId())
                                .setText("New value assigned");
                        getBot().execute(sender);
                    }
                } break;

                default: {

                }
            }
        }

    }

    @Autowired
    public void setKeyboardUtils(KeyboardUtils keyboardUtils) {
        this.keyboardUtils = keyboardUtils;
    }
}
