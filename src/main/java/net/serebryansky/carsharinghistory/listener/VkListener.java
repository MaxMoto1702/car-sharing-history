package net.serebryansky.carsharinghistory.listener;

import com.vk.api.sdk.objects.messages.Message;

public interface VkListener {
    void process(Message message);
}
