package org.adlerzz.apollo.app;

import org.adlerzz.apollo.app.param.Param;
import org.adlerzz.apollo.app.param.ParamsConfig;
import org.adlerzz.apollo.bot.ApolloBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
@ComponentScan("org.adlerzz.apollo")
public class ApolloApplication implements CommandLineRunner {

    private ApolloBot apolloBot;

    private ParamsConfig paramsConfig;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(ApolloApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Param.parse(this.paramsConfig);
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(apolloBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setApolloBot(ApolloBot apolloBot) {
        this.apolloBot = apolloBot;
    }

    @Autowired
    public void setParamsConfig(ParamsConfig paramsConfig) {
        this.paramsConfig = paramsConfig;
    }

}
