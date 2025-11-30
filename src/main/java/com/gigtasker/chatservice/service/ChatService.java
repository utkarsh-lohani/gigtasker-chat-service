package com.gigtasker.chatservice.service;

import com.gigtasker.chatservice.document.ChatMessage;
import com.gigtasker.chatservice.document.ChatRoom;
import com.gigtasker.chatservice.repository.ChatMessageRepository;
import com.gigtasker.chatservice.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    private String generateRoomId(Long taskId, UUID u1, UUID u2) {
        String s1 = u1.toString();
        String s2 = u2.toString();
        return (s1.compareTo(s2) < 0) ?
                String.format("task_%d_%s_%s", taskId, s1, s2) :
                String.format("task_%d_%s_%s", taskId, s2, s1);
    }

    public Optional<String> getChatRoomId(UUID senderId, UUID recipientId, Long taskId, boolean createNew) {
        String consistentId = generateRoomId(taskId, senderId, recipientId);

        return chatRoomRepository.findById(consistentId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(!createNew) return Optional.empty();
                    ChatRoom room = ChatRoom.builder()
                            .id(consistentId)
                            .chatId(consistentId)
                            .senderId(senderId)
                            .recipientId(recipientId)
                            .taskId(taskId)
                            .build();
                    chatRoomRepository.save(room);
                    return Optional.of(consistentId);
                });
    }

    public ChatMessage save(ChatMessage msg) {
        chatMessageRepository.save(msg);
        return msg;
    }

    public List<ChatMessage> findChatMessages(UUID sender, UUID recipient, Long taskId) {
        var chatId = getChatRoomId(sender, recipient, taskId, false);
        return chatId.map(chatMessageRepository::findByChatId).orElse(List.of());
    }

    public List<ChatRoom> getUserChatRooms(UUID userId) {
        return chatRoomRepository.findByUserId(userId);
    }
}
