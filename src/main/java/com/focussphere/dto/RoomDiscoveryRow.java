package com.focussphere.dto;

public class RoomDiscoveryRow {

    private final String roomName;
    private final String roomCode;
    private final String description;
    private final String visibility;
    private final String createdBy;
    private final long memberCount;
    private final long messageCount;
    private final boolean owner;

    public RoomDiscoveryRow(
            String roomName,
            String roomCode,
            String description,
            String visibility,
            String createdBy,
            long memberCount,
            long messageCount,
            boolean owner) {
        this.roomName = roomName;
        this.roomCode = roomCode;
        this.description = description;
        this.visibility = visibility;
        this.createdBy = createdBy;
        this.memberCount = memberCount;
        this.messageCount = messageCount;
        this.owner = owner;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getDescription() {
        return description;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public long getMemberCount() {
        return memberCount;
    }

    public long getMessageCount() {
        return messageCount;
    }

    public boolean isOwner() {
        return owner;
    }
}
