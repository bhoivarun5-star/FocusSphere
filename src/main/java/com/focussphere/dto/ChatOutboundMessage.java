package com.focussphere.dto;

public class ChatOutboundMessage {

    private String senderName;
    private String content;
    private String timestamp;

    public ChatOutboundMessage() {
    }

    public ChatOutboundMessage(String senderName, String content, String timestamp) {
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
