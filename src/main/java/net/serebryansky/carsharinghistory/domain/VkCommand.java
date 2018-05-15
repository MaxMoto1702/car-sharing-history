package net.serebryansky.carsharinghistory.domain;

import java.util.Arrays;

public enum VkCommand {
    LOGIN("Получить логин для доступа через сайт", "логин", "login"),
    PASSWORD("Получить пароль для доступа через сайт", "пароль", "password"),
    EVENTS("Просмотреть историю", "история", "list"),
    UNKNOWN("Неизвестная команда");

    private final String[] text;
    private String description;

    VkCommand(String descritption, String... text) {
        this.description = descritption;
        this.text = text;
    }

    public String[] getText() {
        return text;
    }

    public static VkCommand getByText(String body) {
        if (body == null) return UNKNOWN;
        for (VkCommand command : values()) {
            if (Arrays.asList(command.text).contains(body)) return command;
        }
        return UNKNOWN;
    }

    public String getDescription() {
        return description;
    }
}
