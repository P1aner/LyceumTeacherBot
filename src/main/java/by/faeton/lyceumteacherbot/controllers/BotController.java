package by.faeton.lyceumteacherbot.controllers;

import by.faeton.lyceumteacherbot.config.BotConfig;
import by.faeton.lyceumteacherbot.model.User;
import by.faeton.lyceumteacherbot.repositories.UserRepository;
import by.faeton.lyceumteacherbot.services.SheetService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BotController extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final SheetService sheetService;
    private final UserRepository userRepository;

    private static final String START = """
            Привет!
            Для начала работы необходимо выполнить следующие пункты:
            1. Узнать id своего профиля. Сделать это можно через @userinfobot.
            2. Полученный id, фамилию и класс отправить разработчику бота через старосту класса.
            После внесения вашего id в базу, доступ ко всем функциям бота будет открыт.""";
    private static final String HELP = """
            /start - Начало работы.
            /quarter - Четвертные оценки.
            /marks - Получить оценки.
            /laboratory_notebook - Проверить наличие тетради для лабораторных работ.
            /test_notebook - Проверить наличие тетради для контрольных работ.
            /help - Получить помощь""";
    private static final String AVAILABLE = "В наличии";
    public static final String NOT_AVAILABLE = "Нет в наличии";
    private static final String NOT_AUTHORIZER = "Вы не авторизированы";
    private static final String ANOTHER_MESSAGES = "Для начала работы выполни одну из возможных команд";

    private HashMap<String, String> map = new HashMap<>();

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.getText() != null) {
            String chatId = message.getChatId().toString();
            String receivedMessage = message.getText();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            Optional<User> optionalUser = userRepository.get(chatId);

            if (map.containsKey(chatId)) {
                sendMessage.setText(update.getMessage().getText());
                for (User user : userRepository.getAll()) {
                    String id = user.getId();
                    if (id != null && !id.equals("")) {
                        sendMessage.setChatId(id);
                        try {
                            this.execute(sendMessage);
                        } catch (TelegramApiException e) {

                        }
                    }
                }
                map.clear();
            }

            if (receivedMessage.contains("/send_message")) {
                sendMessage.setText("Что отправляем?");
                map.put(chatId, null);
                this.execute(sendMessage);
            }


            switch (receivedMessage) {
                case "/start" -> sendMessage.setText(arrivedStart());
                case "/marks" -> {
                    if (optionalUser.isPresent()) {
                        sendMessage.setText(arrivedMarks(optionalUser.get()));
                    } else {
                        sendMessage.setText(NOT_AUTHORIZER);
                    }
                }
                case "/quarter" -> {
                    if (optionalUser.isPresent()) {
                        sendMessage.setText(arrivedQuarterMarks(optionalUser.get()));
                    } else {
                        sendMessage.setText(NOT_AUTHORIZER);
                    }
                }
                case "/laboratory_notebook" -> {
                    if (optionalUser.isPresent()) {
                        sendMessage.setText(arrivedLaboratoryNotebook(optionalUser.get()));
                    } else {
                        sendMessage.setText(NOT_AUTHORIZER);
                    }
                }
                case "/test_notebook" -> {
                    if (optionalUser.isPresent()) {
                        sendMessage.setText(arrivedTestNotebook(optionalUser.get()));
                    } else {
                        sendMessage.setText(NOT_AUTHORIZER);
                    }
                }
                case "/help" -> sendMessage.setText(arrivedHelp());
                default -> {
                }
            }
            this.execute(sendMessage);
        }
    }

    private String arrivedStart() {
        return START;
    }

    @SneakyThrows
    private String arrivedMarks(User user) {
        String sheetText = sheetService.getStudentMarks(user);
        if (sheetText.equals("")) {
            return NOT_AVAILABLE;
        }
        return sheetText;
    }

    @SneakyThrows
    private String arrivedQuarterMarks(User user) {
        String sheetText = sheetService.getStudentQuarterMarks(user);
        if (sheetText.equals("")) {
            return NOT_AVAILABLE;
        }
        return sheetText;
    }

    @SneakyThrows
    private String arrivedLaboratoryNotebook(User user) {
        String sheetText = sheetService.getStudentLaboratoryNotebook(user);
        if (sheetText.equals("")) {
            return NOT_AVAILABLE;
        }
        return AVAILABLE;
    }

    @SneakyThrows
    private String arrivedTestNotebook(User userId) {
        String sheetText = sheetService.getStudentTestNotebook(userId);
        if (sheetText.equals("")) {
            return NOT_AVAILABLE;
        }
        return AVAILABLE;
    }

    private String arrivedHelp() {
        return HELP;
    }

    private String arrivedAnother() {
        return ANOTHER_MESSAGES;
    }

    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    public String getBotToken() {
        return botConfig.getBotToken();
    }
}