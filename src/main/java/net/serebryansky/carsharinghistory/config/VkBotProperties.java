package net.serebryansky.carsharinghistory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.bots.vk")
public class VkBotProperties {
    private Integer groupId;
    private String token;
    private Integer adminUserId;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

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
        return "VkBotProperties{" +
                "groupId=" + groupId +
                ", token='" + token + '\'' +
                ", adminUserId=" + adminUserId +
                '}';
    }
}
