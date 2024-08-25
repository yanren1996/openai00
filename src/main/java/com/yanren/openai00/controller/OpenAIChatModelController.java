package com.yanren.openai00.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.function.Consumer;

@RestController
@RequestMapping("/ai/chat-model")
public class OpenAIChatModelController {
    @Autowired
    ChatModel chatModel;

    @GetMapping("/call")
    String call(@RequestParam(value = "message", defaultValue = "你好") String message) {
        return chatModel.call(message);
    }

    @GetMapping("/metadata")
    ChatResponse metadata(@RequestParam(value = "message", defaultValue = "你好") String message) {
        Prompt prompt = new Prompt(message);
        return chatModel.call(prompt);
    }

    @GetMapping("/stream")
    SseEmitter stream(@RequestParam(value = "message", defaultValue = "你好") String message) {
        Flux<String> stream = chatModel.stream(message);
        SseEmitter sse = new SseEmitter();
        stream.subscribe(
                new SseConsumer<>(sse),
                System.err::println,
                () -> {
                    try {
                        sse.send("%end%");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    sse.complete();
                }
        );
        return sse;
    }

    @GetMapping("/stream/metadata")
    SseEmitter streamMetadata(@RequestParam(value = "message", defaultValue = "你好") String message){
        Flux<ChatResponse> stream = chatModel.stream(new Prompt(message));
        SseEmitter sse = new SseEmitter();
        stream.subscribe(
                new SseConsumer<>(sse),
                System.err::println,
                sse::complete
        );
        return sse;
    }

}

class SseConsumer<T> implements Consumer<T>{
    private final SseEmitter sse;
    public SseConsumer(SseEmitter sse){
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
