package ru.liga.springtelegrambot.telegrambot.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import ru.liga.springtelegrambot.telegrambot.client.FeignServer;
import ru.liga.springtelegrambot.telegrambot.commands.buttons.inlinebuttons.InlineKeyboard;
import ru.liga.springtelegrambot.telegrambot.commands.buttons.replybuttons.ButtonKeyboard;
import ru.liga.springtelegrambot.telegrambot.utils.ByteToImage;

@Slf4j
@Component
public class ProfileRegistrationService {

    @Autowired
    private RowProfile rowProfile;
    @Autowired
    private FeignServer feignServer;
    @Autowired
    private ByteToImage byteToImage;
    @Autowired
    private InlineKeyboard inlineKeyboard;

    public SendMessage profileRegistration(Long chatId, Settings userSettings, String text) {
        switch (userSettings.getRegistrationsState()) {
            case NOT_REGISTERED -> {
                return setProfileId(chatId, userSettings, text);
            }
            case GET_GENDER -> {
                return setProfileGender(chatId, userSettings, text);
            }
            case GET_NAME -> {
                return setProfileName(chatId, userSettings, text);
            }
            case GET_DESC -> {
                return setProfileDescription(chatId, userSettings, text);
            }
            case GET_GENDER_SEARCH -> {
                return setProfileGenderSearch(chatId, userSettings, text);
            }
            default -> {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Что-то пошло не так!");
                return sendMessage;
            }
        }
    }

    public SendPhoto profileRegistration(Long chatId, Settings userSettings) {
        SendPhoto sendPhoto = finishRegister(chatId, userSettings);
        sendPhoto.setCaption("Ваш профиль сохранён!\nМожем приступить.");
        userSettings.setRegistrationsState(RegistrationsStates.REGISTERED);
        userSettings.setState(UserStates.MENU);
        ButtonKeyboard.getButtonKeyboard("menu", sendPhoto);
        return sendPhoto;
    }

    private SendMessage setProfileId(Long chatId, Settings userSettings, String text) {
        if (text.equals("YES_BUTTON")) {
            log.info("Создаем профиль и присваиваем chatId");
            rowProfile.addNewProfile(chatId);
            userSettings.setRegistrationsState(RegistrationsStates.GET_GENDER);
            return inlineKeyboard.setInlineButtons(userSettings, chatId);
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Когда надумаете зарегистрироваться напишите /start");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    private SendMessage setProfileGender(Long chatId, Settings userSettings, String text) {
        if (text.equals("MALE_BUTTON")) {
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

    private SendMessage setProfileName(Long chatId,Settings userSettings, String text) {
        rowProfile.setProfileName(chatId, text);
        userSettings.setRegistrationsState(RegistrationsStates.GET_DESC);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Опишите себя");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    private SendMessage setProfileDescription(Long chatId, Settings userSettings, String text) {
        rowProfile.setProfileDescription(chatId, text);
        userSettings.setRegistrationsState(RegistrationsStates.GET_GENDER_SEARCH);
        return inlineKeyboard.setInlineButtons(userSettings, chatId);
    }

    private SendMessage setProfileGenderSearch(Long chatId,Settings userSettings, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        switch (text) {
            case "MALES_BUTTON" -> {
                rowProfile.setProfileGenderSearch(chatId, "сударъ");
                userSettings.setRegistrationsState(RegistrationsStates.GET_PROFILE);
                sendMessage.setText("Вы выбрали сударей");
            }
            case "FEMALES_BUTTON" -> {
                rowProfile.setProfileGenderSearch(chatId, "сударыня");
                userSettings.setRegistrationsState(RegistrationsStates.GET_PROFILE);
                sendMessage.setText("Вы выбрали сударынь");
            }
            case "ALL_BUTTON" -> {
                rowProfile.setProfileGenderSearch(chatId, "все");
                userSettings.setRegistrationsState(RegistrationsStates.GET_PROFILE);
                sendMessage.setText("Вы выбрали всех");
            }
            default -> {
                userSettings.setState(UserStates.UNREGISTERED);
                userSettings.setRegistrationsState(RegistrationsStates.NOT_REGISTERED);
                sendMessage.setText("Неизвестная ошибка, обратитесь к Администратору.");
            }
        }
        return sendMessage;
    }

    private SendPhoto finishRegister(Long chatId, Settings userSettings) {
        ResponseEntity<byte[]> responseEntity = feignServer.setProfile(rowProfile.getProfile(chatId));
        byte[] bytes = responseEntity.getBody();
        return byteToImage.convertByteToImage(bytes, chatId);
    }
}
