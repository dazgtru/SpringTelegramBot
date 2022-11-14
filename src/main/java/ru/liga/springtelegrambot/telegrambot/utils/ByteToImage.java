package ru.liga.springtelegrambot.telegrambot.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.IOException;

@Component
public class ByteToImage {

    public SendPhoto convertByteToImage (byte[] bytes, Long chatId) {
        File filePng;
        String fileName = chatId + "_form.png";

        try {
            FileUtils.writeByteArrayToFile(filePng = new File(fileName), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        InputFile pngFile = new InputFile(filePng, fileName);
        SendPhoto formPng = new SendPhoto(chatId.toString(), pngFile);

        return formPng;
    }
}
