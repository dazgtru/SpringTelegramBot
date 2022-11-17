package ru.liga.springtelegrambot.telegrambot.commands.buttons.inlinebuttons;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.springtelegrambot.telegrambot.data.Settings;
import ru.liga.springtelegrambot.telegrambot.data.UserStates;

import java.util.ArrayList;
import java.util.List;

@Component
public class InlineKeyboard {

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
            case GET_GENDER_SEARCH -> {
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
}
