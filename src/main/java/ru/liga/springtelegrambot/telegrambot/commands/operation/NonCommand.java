package ru.liga.springtelegrambot.telegrambot.commands.operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.liga.springtelegrambot.telegrambot.Bot;
import ru.liga.springtelegrambot.telegrambot.client.feign.FeignServer;
import ru.liga.springtelegrambot.telegrambot.data.*;
import ru.liga.springtelegrambot.telegrambot.utils.ByteToImage;

@Slf4j
@Component
public class NonCommand {

    @Autowired
    private RowProfile rowProfile;
    @Autowired
    private FeignServer feignServer;
    @Autowired
    private ByteToImage byteToImage;


    public void NonCommand(@Autowired RowProfile rowProfile) {
        this.rowProfile = rowProfile;
    }

//    public void setProfileGender(Message message) {
//        Long chatId = message.getChatId();
//        Settings userSettings = Bot.getUserSettings(chatId);
//
//        switch (userSettings.getRegistrationsState()) {
//            case GET_NAME -> {
//                rowProfile.setProfileName(chatId, message.getText());
//                userSettings.setRegistrationsState(RegistrationsStates.GET_DESC);
//                sendMessage(chatId, "Опишите себя(при этом первая строка - заголовок, последующие - описание)");
//            }
//
//            case GET_DESC -> {
//                rowProfile.setProfileDescription(chatId, message.getText());
//                userSettings.setRegistrationsState(RegistrationsStates.GET_DESC);
//                ProfileRegistrationService profileRegistrationService = new ProfileRegistrationService();
//                SendMessage
//                sendMessage(profileRegistrationService.registration(userSettings, chatId));
//            }
//
//            default -> {
//                String answer = "Напишите или выберите команду из кнопок.";
//                sendMessage(chatId, answer);
//            }
//        }
//    }

    public SendMessage getProfileRegistrationService(Long chatId, Settings userSettings, CallbackQuery callbackQuery) {
        ProfileRegistrationService profileRegistrationService = new ProfileRegistrationService();
        if (callbackQuery.getData().equals("YES_BUTTON")) {
            log.info("Создаем профиль и присваиваем chatId");
            rowProfile.addNewProfile(chatId);
            userSettings.setRegistrationsState(RegistrationsStates.GET_GENDER);
            return profileRegistrationService.registration(userSettings, chatId);
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Когда надумаете зарегистрироваться напишите /start");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

//    public SendMessage getProfileRegistrationService(Long chatId, Settings userSettings, Message message) {
//        ProfileRegistrationService profileRegistrationService = new ProfileRegistrationService();
//        rowProfile.setProfileDescription(chatId, message.getText());
//        userSettings.setRegistrationsState(RegistrationsStates.GET_DESC);
//        return profileRegistrationService.registration(userSettings, chatId);
//    }

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
        SendMessage sendMessage = new SendMessage();
        ProfileRegistrationService profileRegistrationService = new ProfileRegistrationService();
        return profileRegistrationService.registration(userSettings, chatId);
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
            default -> {
                sendMessage.setText("Неизвестная ошибка, обратитесь к Администратору.");
            }
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
