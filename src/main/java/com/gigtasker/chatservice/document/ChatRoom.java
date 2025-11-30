package com.gigtasker.chatservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "chat_rooms")
public class ChatRoom {
    @Id
    private String id;

    private Long taskId; // Context: Which gig are they discussing?
    private String chatId; // Unique ID (e.g. "task_101_poster_worker")
    private UUID senderId;
    private UUID recipientId;
}
