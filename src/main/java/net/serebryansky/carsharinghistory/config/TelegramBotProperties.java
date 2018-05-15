package net.serebryansky.carsharinghistory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.bots.telegram")
public class TelegramBotProperties {
    private String token;
    private Integer adminUserId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Integer adminUserId) {
        this.adminUserId = adminUserId;
    }

    @Override
    public String toString() {
        return "TelegramBotProperties{" +
                "token='" + token + '\'' +
                ", adminUserId=" + adminUserId +
                '}';
    }
}
