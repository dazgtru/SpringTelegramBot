package ru.liga.springtelegrambot.telegrambot.data;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class RowProfile {

    Map<Long, Profile> profileList = new HashMap<>();

    public void addNewProfile(Long chatId) {
        Profile profile = new Profile();
        profile.setChatId(chatId);
        profile.setSearch(0L);
        profile.setLovers(0L);
        profileList.put(chatId, profile);
    }

    public void setProfileGender(Long chatId, String gender) {
        profileList.get(chatId).setGender(gender);
    }

    public void setProfileGenderSearch(Long chatId, String gender) {
        profileList.get(chatId).setGenderSearch(gender);
    }

    public void setProfileName(Long chatId, String name) {
        profileList.get(chatId).setName(name);
    }

    public void setProfileDescription(Long chatId, String desc) {
        profileList.get(chatId).setDescription(desc);
    }

    public Profile getProfile(Long chatId) {
        Profile profile = profileList.get(chatId);
        profileList.remove(chatId);
        return profile;
    }
}
