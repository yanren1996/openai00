package com.yanren.openai00.controller;

import com.yanren.openai00.consumer.SseConsumer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai/chat-client")
public class OpenAIChatClientController {

    final ChatClient chatClient;

    @Autowired
    public OpenAIChatClientController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping
    String call(
            @RequestParam(value = "message",defaultValue = "你好")String message,
            @RequestParam(value = "system",defaultValue = "你喜換使用顏文字、你說話前會告訴別人你需要先想一想...")String system
    ){
        return chatClient.prompt()
                .system(system)
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/stream")
    SseEmitter stream(
            @RequestParam(value = "message",defaultValue = "你好")String message,
            @RequestParam(value = "system",defaultValue = "你是個喜換使用顏文字的ai，另外你說話前會告訴別人你需要先想一想...")String system
    ){
        SseEmitter sse = new SseEmitter();
        Flux<String> stream = chatClient.prompt()
                .system(system)
                .user(message)
                .stream()
                .content();
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

}
