package by.faeton.lyceumteacherbot.config;


import by.faeton.lyceumteacherbot.controllers.BotController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class Initializer {

    private static final Logger log = LoggerFactory.getLogger(Initializer.class);

    private final BotController botController;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot((LongPollingBot) botController);
            log.debug("Telegram Bot Registered");
        } catch (TelegramApiException e) {
            log.error("Telegram Bot Not Registered" + e);
            throw new RuntimeException(e);
        }

    }
}