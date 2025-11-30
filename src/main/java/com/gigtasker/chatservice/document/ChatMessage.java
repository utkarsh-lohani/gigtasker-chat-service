package com.gigtasker.chatservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String id;

    private String chatId; // Links to ChatRoom
    private Long taskId;

    private UUID senderId;
    private UUID recipientId;

    private String content;
    private LocalDateTime timestamp;
}
