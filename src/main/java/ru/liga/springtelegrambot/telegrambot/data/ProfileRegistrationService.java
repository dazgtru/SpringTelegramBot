package ru.liga.springtelegrambot.telegrambot.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.springtelegrambot.telegrambot.client.FeignServer;
import ru.liga.springtelegrambot.telegrambot.utils.ByteToImage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ProfileRegistrationService {

    @Autowired
    private RowProfile rowProfile;
    @Autowired
    private FeignServer feignServer;
    @Autowired
    private ByteToImage byteToImage;

    public SendMessage setInlineButtons(Settings userSettings, Long chatId) {
        userSettings.setState(UserStates.REGISTRATION);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var firstButton = new InlineKeyboardButton();
        var secondButton = new InlineKeyboardButton();
        var thirdButton = new InlineKeyboardButton();
        switch (userSettings.getRegistrationsState()) {
            case NOT_REGISTERED -> {
                message.setText("Вы действительно хотите зарегистрироваться?");
                firstButton.setText("Да");
                firstButton.setCallbackData("YES_BUTTON");

                secondButton.setText("Нет");
                secondButton.setCallbackData("NO_BUTTON");

                rowInLine.add(firstButton);
                rowInLine.add(secondButton);
            }
            case GET_GENDER -> {
                message.setText("Вы сударъ или сударыня?");
                firstButton.setText("Сударъ");
                firstButton.setCallbackData("MALE_BUTTON");

                secondButton.setText("Сударыня");
                secondButton.setCallbackData("FEMALE_BUTTON");

                rowInLine.add(firstButton);
                rowInLine.add(secondButton);
            }
            case GET_DESC -> {
                message.setText("Кого вы ищите?");
                firstButton.setText("Сударъ");
                firstButton.setCallbackData("MALES_BUTTON");

                secondButton.setText("Сударыня");
                secondButton.setCallbackData("FEMALES_BUTTON");

                thirdButton.setText("Всех");
                thirdButton.setCallbackData("ALL_BUTTON");

                rowInLine.add(firstButton);
                rowInLine.add(secondButton);
                rowInLine.add(thirdButton);
            }
        }

        rowsInLine.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(inlineKeyboardMarkup);

        return message;
    }

    public SendMessage setProfileId(Long chatId, Settings userSettings, CallbackQuery callbackQuery) {
        if (callbackQuery.getData().equals("YES_BUTTON")) {
            log.info("Создаем профиль и присваиваем chatId");
            rowProfile.addNewProfile(chatId);
            userSettings.setRegistrationsState(RegistrationsStates.GET_GENDER);
            return setInlineButtons(userSettings, chatId);
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Когда надумаете зарегистрироваться напишите /start");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public SendMessage setProfileGender(Long chatId, Settings userSettings, CallbackQuery callback) {
        if (callback.getData().equals("MALE_BUTTON")) {
            rowProfile.setProfileGender(chatId, "сударъ");
        } else {
            rowProfile.setProfileGender(chatId, "сударыня");
        }
        userSettings.setRegistrationsState(RegistrationsStates.GET_NAME);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Как вас величать?");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public SendMessage setProfileName(Long chatId,Settings userSettings, Message message) {
        rowProfile.setProfileName(chatId, message.getText());
        userSettings.setRegistrationsState(RegistrationsStates.GET_DESC);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Опишите себя(при этом первая строка - заголовок, последующие - описание)");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public SendMessage setProfileDescription(Long chatId, Settings userSettings, Message message) {
        rowProfile.setProfileDescription(chatId, message.getText());
        userSettings.setRegistrationsState(RegistrationsStates.GET_DESC);
        return setInlineButtons(userSettings, chatId);
    }

    public SendMessage setProfileGenderSearch(Long chatId, CallbackQuery callbackQuery) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        switch (callbackQuery.getData()) {
            case "MALES_BUTTON" -> {
                rowProfile.setProfileGenderSearch(chatId, "сударъ");
                sendMessage.setText("Вы выбрали сударей");
            }
            case "FEMALES_BUTTON" -> {
                rowProfile.setProfileGenderSearch(chatId, "сударыня");
                sendMessage.setText("Вы выбрали сударынь");
            }
            case "ALL_BUTTON" -> {
                rowProfile.setProfileGenderSearch(chatId, "все");
                sendMessage.setText("Вы выбрали всех");
            }
            default ->
                sendMessage.setText("Неизвестная ошибка, обратитесь к Администратору.");
        }
        return sendMessage;
    }

    public SendPhoto finishRegister(Long chatId, Settings userSettings) {

        ResponseEntity<byte[]> responseEntity = feignServer.setProfile(rowProfile.getProfile(chatId));
        byte[] bytes = responseEntity.getBody();
        userSettings.setRegistrationsState(RegistrationsStates.REGISTERED);
        userSettings.setState(UserStates.MENU);
        return byteToImage.convertByteToImage(bytes, chatId);
    }
}
