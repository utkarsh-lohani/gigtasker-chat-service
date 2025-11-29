package com.gigtasker.chatservice.controller;

import com.gigtasker.chatservice.dto.ChatNotification;
import com.gigtasker.chatservice.entity.ChatMessage;
import com.gigtasker.chatservice.entity.ChatRoom;
import com.gigtasker.chatservice.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate, ChatService chatService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage, Principal principal) {
        if (principal == null) {
            log.error("❌ Message rejected: No Principal found in session.");
            return;
        }

        var chatId = chatService.getChatRoomId(
                chatMessage.getSenderId(),
                chatMessage.getRecipientId(),
                chatMessage.getTaskId(),
                true
        );
        chatMessage.setChatId(chatId.get());
        chatService.save(chatMessage);

        simpMessagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId().toString(),
                "/queue/messages",
                new ChatNotification(chatMessage.getId(), chatMessage.getSenderId(), chatMessage.getContent())
        );
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getMyRooms(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(chatService.getUserChatRooms(UUID.fromString(jwt.getClaimAsString("sub"))));
    }

    @GetMapping("/messages/{taskId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> getMessages(
            @PathVariable Long taskId,
            @PathVariable UUID recipientId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID senderId = UUID.fromString(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok(chatService.findChatMessages(senderId, recipientId, taskId));
    }
}
