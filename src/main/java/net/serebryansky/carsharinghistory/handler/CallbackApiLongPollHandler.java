package net.serebryansky.carsharinghistory.handler;

import com.vk.api.sdk.callback.longpoll.CallbackApiLongPoll;
import com.vk.api.sdk.callback.objects.group.CallbackGroupJoin;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.messages.Message;
import net.serebryansky.carsharinghistory.domain.User;
import net.serebryansky.carsharinghistory.listener.VkListener;
import net.serebryansky.carsharinghistory.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CallbackApiLongPollHandler extends CallbackApiLongPoll {
    private static final Logger log = LoggerFactory.getLogger(CallbackApiLongPollHandler.class);

    private final UserService userService;
    private final VkListener listener;

    public CallbackApiLongPollHandler(VkApiClient client, GroupActor actor, UserService userService, VkListener vkListener) {
        super(client, actor);
        this.userService = userService;
        this.listener = vkListener;
    }

    public void messageNew(Integer groupId, Message message) {
        log.info("messageNew: " + message.toString());
//        log_(message, 1);
        User user = userService.findByVkUserId(message.getUserId());
        if (user != null) {
            Authentication userAuth = new UsernamePasswordAuthenticationToken(user, "vk");
            SecurityContextHolder.getContext().setAuthentication(userAuth);
        }
        listener.process(message);
    }

    private void log_(Message message, int level) {
        log.info("LOG MESSAGE ({}): {}", level, message.toString().replace("\n", "\\n"));
        if (message.getFwdMessages() != null) {
            for (Message fwdMessage : message.getFwdMessages()) {
                log_(fwdMessage, level + 1);
            }
        }
    }

    @Override
    public void groupJoin(Integer groupId, CallbackGroupJoin message) {
        log.info("Group join: " + message.toString());
        userService.createFromVk(message.getUserId());
    }

    @Override
    public void groupJoin(Integer groupId, String secret, CallbackGroupJoin message) {
        log.info("Group join: " + message.toString());
        userService.createFromVk(message.getUserId(), secret);
    }
}
