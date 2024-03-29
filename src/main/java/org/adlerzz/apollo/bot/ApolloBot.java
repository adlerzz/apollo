package org.adlerzz.apollo.bot;

import org.adlerzz.apollo.bot.handlers.CommandHandler;
import org.adlerzz.apollo.bot.handlers.ImageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import static org.adlerzz.apollo.app.param.Param.BOT_NAME;
import static org.adlerzz.apollo.app.param.Param.BOT_TOKEN;

@Component
public class ApolloBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(ApolloBot.class);

    private ImageHandler imageHandler;
    private CommandHandler commandHandler;

    public ApolloBot(){

    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {

                Message message = update.getMessage();
                log.debug("get message from {} ({})", message.getChatId(), message.getChat().getUserName());
                if (update.getMessage().hasPhoto()) {
                    imageHandler.accept(message);
                }
                if (update.getMessage().hasText()) {
                    commandHandler.accept(message);
                }
            }

            if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                commandHandler.react(callbackQuery);
            }
        } catch(TelegramApiRequestException e) {
            log.error("api response: {}", e.getApiResponse());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return BOT_NAME.getValue();
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN.getValue();
    }

    @Autowired
    public void setImageHandler(ImageHandler imageHandler) {
        this.imageHandler = imageHandler;
    }

    @Autowired
    public void setCommandHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

}
