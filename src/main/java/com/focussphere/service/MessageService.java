package com.focussphere.service;

import com.focussphere.dto.ChatOutboundMessage;
import com.focussphere.model.Message;
import com.focussphere.model.Room;
import com.focussphere.model.User;
import com.focussphere.repository.MessageRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public ChatOutboundMessage saveAndBuild(Room room, User sender, String content) {
        Message message = new Message();
        message.setRoom(room);
        message.setSender(sender);
        message.setContent(content.trim());
        message.setSentAt(LocalDateTime.now());
        Message saved = messageRepository.save(message);

        String ts = saved.getSentAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new ChatOutboundMessage(sender.getName(), saved.getContent(), ts);
    }

    public long getMessageCount(Room room) {
        return messageRepository.countByRoom(room);
    }

    public List<Message> getMessagesByRoom(Room room) {
        return messageRepository.findByRoomOrderBySentAtAsc(room);
    }
}
