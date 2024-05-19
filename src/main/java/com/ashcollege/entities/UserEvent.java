package com.ashcollege.entities;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class UserEvent {


    private SseEmitter sseEmitter;
    private String token;
    private String type;


    public UserEvent(SseEmitter sseEmitter, String secret, String type) {
        this.sseEmitter = sseEmitter;
        this.token = secret;
        this.type=type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SseEmitter getSseEmitter() {
        return sseEmitter;
    }

    public void setSseEmitter(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}