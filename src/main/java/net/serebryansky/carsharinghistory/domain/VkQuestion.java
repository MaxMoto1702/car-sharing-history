package net.serebryansky.carsharinghistory.domain;

import java.util.Arrays;

public enum  VkQuestion {
    SELECT_EVENT_NUMBER("Введите номер события, которое хотите просмотреть (ответьте на это сообщение)");

    private final String[] text;

    VkQuestion(String... text) {
        this.text = text;
    }

    public String[] getText() {
        return text;
    }

    public static VkQuestion getByText(String body) {
        if (body == null) return null;
        for (VkQuestion question : values()) {
            if (Arrays.asList(question.text).contains(body)) return question;
        }
        return null;
    }
}
