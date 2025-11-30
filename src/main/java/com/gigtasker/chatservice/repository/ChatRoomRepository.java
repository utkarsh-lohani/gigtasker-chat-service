package com.gigtasker.chatservice.repository;

import com.gigtasker.chatservice.document.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom,String> {
    @Query("{ '$or': [ { 'senderId': ?0 }, { 'recipientId': ?0 } ] }")
    List<ChatRoom> findByUserId(UUID userId);
}
