package ru.liga.springtelegrambot.telegrambot.commands.buttons.replybuttons;


public enum Commands {
    START("/start", "Старт"),
    HELP("/help", "Помощь"),
    MENU("/menu", "Меню"),
    SEARCH("/search", "Поиск"),
    PROFILE("/profile", "Анкета"),
    LOVERS("/lovers", "Любимцы"),
    EDIT("/edit", "Редактировать"),
    LEFT("/left", "Влево"),
    RIGHT("/right", "Вправо");

    public final String command;
    public final String description;

    Commands(final String command, final String description) {
        this.command = command;
        this.description = description;
    }
}
