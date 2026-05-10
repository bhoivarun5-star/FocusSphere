package com.focussphere.controller;

import com.focussphere.dto.ChatInboundMessage;
import com.focussphere.dto.ChatOutboundMessage;
import com.focussphere.model.NotificationType;
import com.focussphere.model.Room;
import com.focussphere.model.User;
import com.focussphere.service.MessageService;
import com.focussphere.service.NotificationService;
import com.focussphere.service.RoomService;
import com.focussphere.service.UserService;
import java.util.List;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final RoomService roomService;
    private final UserService userService;
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(
            RoomService roomService,
            UserService userService,
            MessageService messageService,
            NotificationService notificationService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.roomService = roomService;
        this.userService = userService;
        this.messageService = messageService;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(ChatInboundMessage inboundMessage) {
        if (inboundMessage == null) {
            return;
        }

        String roomCode = sanitize(inboundMessage.getRoomCode()).toUpperCase();
        String senderEmail = sanitize(inboundMessage.getSenderEmail());
        String content = sanitize(inboundMessage.getContent());

        if (roomCode.isEmpty() || senderEmail.isEmpty() || content.isEmpty()) {
            return;
        }

        Room room = roomService.findByRoomCode(roomCode).orElse(null);
        User sender = userService.findByEmail(senderEmail).orElse(null);
        if (room == null || sender == null) {
            return;
        }
        if (!roomService.isUserMember(room, sender)) {
            return;
        }

        ChatOutboundMessage outbound = messageService.saveAndBuild(room, sender, content);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, outbound);

        // Create notifications for all other members in the room
        try {
            List<User> roomMembers = roomService.getRoomMembers(room);
            for (User recipient : roomMembers) {
                // Don't send notification to the sender
                if (!recipient.getId().equals(sender.getId())) {
                    notificationService.createNotification(
                        recipient,
                        NotificationType.MESSAGE,
                        "New message from " + sender.getName(),
                        "New message in room: " + content.substring(0, Math.min(50, content.length())),
                        room,
                        sender
                    );
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the message sending
            System.err.println("Error creating notifications: " + e.getMessage());
        }
    }

    private String sanitize(String value) {
        return value == null ? "" : value.trim();
    }
}

