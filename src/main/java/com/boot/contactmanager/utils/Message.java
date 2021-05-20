package com.boot.contactmanager.utils;

import lombok.Data;

// flash messages
@Data
public class Message {

    private String content;

    // error / success
    private String type;

    public Message(String content, String type) {
        this.content = content;
        this.type = type;
    }

}
