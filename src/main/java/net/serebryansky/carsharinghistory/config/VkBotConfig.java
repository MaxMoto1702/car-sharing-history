package net.serebryansky.carsharinghistory.config;

import com.vk.api.sdk.callback.longpoll.CallbackApiLongPoll;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.responses.GetMembersResponse;
import net.serebryansky.carsharinghistory.handler.CallbackApiLongPollHandler;
import net.serebryansky.carsharinghistory.listener.VkListener;
import net.serebryansky.carsharinghistory.listener.VkListenerImpl;
import net.serebryansky.carsharinghistory.repository.EventRepository;
import net.serebryansky.carsharinghistory.service.UserService;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties(VkBotProperties.class)
public class VkBotConfig {
    private static final Logger log = LoggerFactory.getLogger(VkBotConfig.class);

    private final VkBotProperties properties;
    private final UserService userService;
    private final EventRepository eventRepository;

    public VkBotConfig(VkBotProperties properties, UserService userService, EventRepository eventRepository) {
        this.properties = properties;
        this.userService = userService;
        this.eventRepository = eventRepository;
    }

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public VkListener vkListener() throws ClientException, ApiException {
        return new VkListenerImpl(vkApiClient(), groupActor(), random(), userService, eventRepository, httpClient());
    }

    @Bean
    public CallbackApiLongPoll callbackApiLongPoll() throws ClientException, ApiException {
        return new CallbackApiLongPollHandler(vkApiClient(), groupActor(), userService, vkListener());
    }

    @Bean
    public GroupActor groupActor() {
        return new GroupActor(properties.getGroupId(), properties.getToken());
    }

    @Bean
    public VkApiClient vkApiClient() throws ClientException, ApiException {
        VkApiClient client = new VkApiClient(transportClient());
        if (!client.groups().getLongPollSettings(groupActor()).execute().isEnabled()) {
            client.groups()
                    .setLongPollSettings(groupActor())
                    .enabled(true)
                    .wallPostNew(true)
                    .execute();
        }
        return client;
    }

    @Bean
    public TransportClient transportClient() {
        return HttpTransportClient.getInstance();
    }

    @PostConstruct
    public void confUsers() throws ClientException, ApiException {
        VkApiClient vkApiClient = vkApiClient();
        GroupActor groupActor = groupActor();
        GetMembersResponse res = vkApiClient
                .groups()
                .getMembers(groupActor)
                .groupId(String.valueOf(groupActor.getGroupId()))
                .execute();
        res.getItems().forEach(userService::createFromVk);
        log.debug("Result: {}", res);

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            while (true) {
                try {
                    callbackApiLongPoll().run();
                } catch (ClientException | ApiException e) {
                    log.error("Cannot run handler", e);
                }
            }
        });
    }

    @Bean
    public CloseableHttpClient httpClient() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        return HttpClients.custom()
                .setConnectionManager(cm)
                .build();
    }
}
