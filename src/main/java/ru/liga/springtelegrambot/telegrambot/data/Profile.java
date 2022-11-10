package ru.liga.springtelegrambot.telegrambot.data;

import lombok.Data;

@Data
public class Profile implements Cloneable {
    private Long chatId;

    private String name;

    private String gender;

    private String description;

    private String genderSearch;

    private Long search;

    private Long lovers;

    public Profile(Long chatId, String name, String gender, String description, String genderSearch, Long search, Long lovers) {
        this.chatId = chatId;
        this.name = name;
        this.gender = gender;
        this.description = description;
        this.genderSearch = genderSearch;
        this.search = search;
        this.lovers = lovers;
    }

    public Profile() {
    }

    public Profile clone() {
        Profile profile = new Profile(this.getChatId(),
                this.getName(),
                this.getGender(),
                this.getDescription(),
                this.getGenderSearch(),
                this.getSearch(),
                this.getLovers());
        return profile;
    }
}
