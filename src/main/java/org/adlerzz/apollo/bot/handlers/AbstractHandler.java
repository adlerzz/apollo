package org.adlerzz.apollo.bot.handlers;

import org.adlerzz.apollo.bot.ApolloBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public abstract class AbstractHandler {
    protected ApolloBot bot;

    public ApolloBot getBot() {
        return bot;
    }

    @Autowired
    public void setBot(ApolloBot bot) {
        this.bot = bot;
    }

    public abstract void accept(Message message) throws TelegramApiException;

}
