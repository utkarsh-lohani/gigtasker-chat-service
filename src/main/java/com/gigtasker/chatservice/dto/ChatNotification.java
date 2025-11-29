package com.gigtasker.chatservice.dto;

import java.util.UUID;
public record ChatNotification(String id, UUID senderId, String content) {}
