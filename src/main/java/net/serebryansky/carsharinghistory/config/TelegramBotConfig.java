package net.serebryansky.carsharinghistory.config;

import com.pengrad.telegrambot.TelegramBot;
import net.serebryansky.carsharinghistory.listener.TelegramListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TelegramBotProperties.class)
public class TelegramBotConfig {

    private final TelegramBotProperties properties;

    public TelegramBotConfig(TelegramBotProperties properties) {
        this.properties = properties;
    }

    @Bean
    public TelegramListener telegramListener() {
//        return new TelegramListener(telegramBot());
        TelegramBot bot = telegramBot();
        TelegramListener listener = new TelegramListener(bot);
        bot.setUpdatesListener(listener);
        return listener;
    }

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(properties.getToken());
    }
}
