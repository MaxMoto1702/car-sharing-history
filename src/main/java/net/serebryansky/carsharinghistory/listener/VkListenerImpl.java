package net.serebryansky.carsharinghistory.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.MessageAttachment;
import com.vk.api.sdk.objects.photos.PhotoUpload;
import net.serebryansky.carsharinghistory.domain.*;
import net.serebryansky.carsharinghistory.repository.EventRepository;
import net.serebryansky.carsharinghistory.service.UserService;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class VkListenerImpl implements VkListener {
    private static final Logger log = LoggerFactory.getLogger(VkListenerImpl.class);

    private final GroupActor groupActor;
    private final Random random;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final CloseableHttpClient httpClient;
    private final VkApiClient client;

    public VkListenerImpl(VkApiClient client, GroupActor actor, Random random, UserService userService, EventRepository eventRepository, CloseableHttpClient httpClient) {
        this.client = client;
        this.groupActor = actor;
        this.random = random;
        this.userService = userService;
        this.eventRepository = eventRepository;
        this.httpClient = httpClient;
    }

    @Transactional
    public void process(Message message) {
        try {
            if (!isAuthenticated()) {
                noAuthorize(message);
                return;
            }
            if (message.getBody() != null && (message.getBody().equals("добавить") || message.getBody().equals("+"))
                    && message.getAttachments() != null && !message.getAttachments().isEmpty()) {
                Event event = eventRepository.findLast().get(0);
                event.setDate(new Date());
                save(event, message.getAttachments());
                client.messages()
                        .send(groupActor)
                        .message("Фото добавлены")
                        .userId(message.getUserId())
                        .randomId(random.nextInt())
                        .execute();
                client.messages()
                        .send(groupActor)
                        .message("Добавить фото? (написшите сообщение 'добавить' или '+' с дополнительными фото)")
                        .userId(message.getUserId())
                        .randomId(random.nextInt())
                        .execute();
            } else if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
                Event event = new Event();
                event.setDate(new Date());
                event.setOwner(getCurrentUser());
                event.setPhotos(new HashSet<>());
                save(event, message.getAttachments());
                client.messages()
                        .send(groupActor)
                        .message("Фото сохранены")
                        .userId(message.getUserId())
                        .randomId(random.nextInt())
                        .execute();
                client.messages()
                        .send(groupActor)
                        .message("Добавить фото? (написшите сообщение 'добавить' или '+' с дополнительными фото)")
                        .userId(message.getUserId())
                        .randomId(random.nextInt())
                        .execute();
            } else if (message.getFwdMessages() != null && !message.getFwdMessages().isEmpty()) {
                List<Message> fwdMessages = message.getFwdMessages();
                Message fwdMessage = fwdMessages.get(0);
                switch (VkQuestion.getByText(fwdMessage.getBody())) {
                    case SELECT_EVENT_NUMBER:
                        Long id = Long.valueOf(message.getBody());
                        if (eventRepository.existsById(id)) {
                            Event event = eventRepository.getOne(id);
                            for (Photo photo : event.getPhotos()) {
                                PhotoUpload r = client.photos()
                                        .getMessagesUploadServer(groupActor)
                                        .peerId(message.getUserId())
                                        .execute();
                                HttpPost photoRequest = new HttpPost(r.getUploadUrl());
//                                photoRequest.setEntity(MultipartEntityBuilder.create().addBinaryBody("photo", photo.getBytes()).build());
                                photoRequest.setEntity(MultipartEntityBuilder.create().addPart("file", new ByteArrayBody(photo.getBytes(), "photo.jpg")).build());
                                CloseableHttpResponse photoResponse = httpClient.execute(photoRequest);
                                InputStream photoStream = photoResponse.getEntity().getContent();
                                ObjectMapper mapper = new ObjectMapper();
                                Map value = mapper.readValue(photoStream, Map.class);
                                List<com.vk.api.sdk.objects.photos.Photo> r2 = client.photos()
                                        .saveMessagesPhoto(groupActor, String.valueOf(value.get("photo")))
                                        .hash(String.valueOf(value.get("hash")))
                                        .server((Integer) value.get("server"))
                                        .execute();
                                r2.get(0).getId();
                                client.messages()
                                        .send(groupActor)
                                        .userId(message.getUserId())
                                        .attachment("photo" + r2.get(0).getOwnerId() + "_" + r2.get(0).getId())
                                        .execute();
                            }
                        } else {
                            client.messages()
                                    .send(groupActor)
                                    .message("Вывели неверное значение")
                                    .userId(message.getUserId())
                                    .randomId(random.nextInt())
                                    .execute();
                            client.messages()
                                    .send(groupActor)
                                    .message(VkQuestion.SELECT_EVENT_NUMBER.getText()[0])
                                    .userId(message.getUserId())
                                    .randomId(random.nextInt())
                                    .execute();
                        }
                        break;
                }
            } else {
                switch (VkCommand.getByText(message.getBody())) {
                    case LOGIN:
                        client.messages()
                                .send(groupActor)
                                .message(Objects.requireNonNull(getCurrentUser()).getUsername())
                                .userId(message.getUserId())
                                .randomId(random.nextInt())
                                .execute();
                        break;
                    case PASSWORD:
                        User user = Objects.requireNonNull(getCurrentUser());
                        String password = UUID.randomUUID().toString();
                        user.setPassword(password);
                        userService.save(user);
                        client.messages()
                                .send(groupActor)
                                .message(password)
                                .userId(message.getUserId())
                                .randomId(random.nextInt())
                                .execute();
                        break;
                    case EVENTS:
                        StringBuilder builder = new StringBuilder("События:\n");
                        if (eventRepository.count() > 0) {
                            eventRepository.findAll().forEach(event -> builder.append(event.getId()).append(") ").append(event.getDate()).append("\n"));
                            client.messages()
                                    .send(groupActor)
                                    .message(builder.toString())
                                    .userId(message.getUserId())
                                    .randomId(random.nextInt())
                                    .execute();
                            client.messages()
                                    .send(groupActor)
                                    .message(VkQuestion.SELECT_EVENT_NUMBER.getText()[0])
                                    .userId(message.getUserId())
                                    .randomId(random.nextInt())
                                    .execute();
                        } else {
                            client.messages()
                                    .send(groupActor)
                                    .message("Событий нет")
                                    .userId(message.getUserId())
                                    .randomId(random.nextInt())
                                    .execute();
                        }
                        break;
                    case UNKNOWN:
                        unknownRequest(message);
                }
            }
        } catch (Exception e) {
            log.error("Message: {}", message);
            log.error("ERROR", e);
        }
    }

    private void save(Event event, List<MessageAttachment> attachments) throws IOException {
        for (MessageAttachment attachment : attachments) {
            log.info("Attach: {}", attachment);
            Photo photo = new Photo();
            String photoUri = attachment.getPhoto().getPhoto2560();
            if (photoUri == null) photoUri = attachment.getPhoto().getPhoto1280();
            if (photoUri == null) photoUri = attachment.getPhoto().getPhoto807();
            if (photoUri == null) photoUri = attachment.getPhoto().getPhoto604();
            if (photoUri == null) photoUri = attachment.getPhoto().getPhoto130();
            if (photoUri == null) photoUri = attachment.getPhoto().getPhoto75();
            if (photoUri == null) continue;
            HttpGet photoRequest = new HttpGet(photoUri);
            CloseableHttpResponse photoResponse = httpClient.execute(photoRequest);
            InputStream photoStream = photoResponse.getEntity().getContent();
            byte[] photoBytes = IOUtils.toByteArray(photoStream);
            photo.setBytes(photoBytes);
            event.getPhotos().add(photo);
        }
        eventRepository.save(event);
    }

    private void unknownRequest(Message message) throws ClientException, ApiException {
//        String value = "Я вас не понимаю. Я понимаю следующие команды:\n" +
//                " - фотографии - создать событие\n" +
//                " - /gen-username - генерирование имени пользователя\n" +
//                " - /reset-password - сброс пароля\n" +
//                " - /list - список событий\n" +
//                " - /many - много сообщений\n";
        StringBuilder valueBuilder = new StringBuilder("Доступные команды:\n");
        valueBuilder.append(" - фотографии - создать событие\n");
        for (VkCommand command : VkCommand.values()) {
            if (command == VkCommand.UNKNOWN) continue;
            valueBuilder.append(" - ")
                    .append(command.getText()[0])
                    .append(" - ")
                    .append(command.getDescription())
                    .append("\n");
        }
        client.messages()
                .send(groupActor)
                .message(valueBuilder.toString())
                .userId(message.getUserId())
                .randomId(random.nextInt())
                .execute();
    }

    private void noAuthorize(Message message) throws ClientException, ApiException {
        client
                .messages()
                .send(groupActor)
                .message("Вы не зарегистрированы.\n" +
                        "Для регситрации вступите в группу.")
                .userId(message.getUserId())
                .randomId(random.nextInt())
                .execute();
    }

    private boolean isAuthenticated() {
//        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    private User getCurrentUser() {
        if (isAuthenticated()) {
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } else {
            return null;
        }
    }
}
