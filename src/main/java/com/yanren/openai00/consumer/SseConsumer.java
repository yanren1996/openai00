package com.yanren.openai00.consumer;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.function.Consumer;

public class SseConsumer<T> implements Consumer<T> {
    private final SseEmitter sse;

    public SseConsumer(SseEmitter sse) {
        this.sse = sse;
    }

    @Override
    public void accept(T message) {
        try {
            sse.send(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
