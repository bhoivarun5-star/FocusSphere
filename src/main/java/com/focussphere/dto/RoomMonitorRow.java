package com.focussphere.dto;

public class RoomMonitorRow {

    private String roomName;
    private String roomCode;
    private String visibility;
    private String createdBy;
    private long memberCount;
    private long messageCount;

    public RoomMonitorRow(String roomName, String roomCode, String visibility, String createdBy, long memberCount, long messageCount) {
        this.roomName = roomName;
        this.roomCode = roomCode;
        this.visibility = visibility;
        this.createdBy = createdBy;
        this.memberCount = memberCount;
        this.messageCount = messageCount;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomCode() {
        return roomCode;
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
}
