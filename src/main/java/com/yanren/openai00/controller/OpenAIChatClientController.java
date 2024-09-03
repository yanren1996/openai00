package com.yanren.openai00.controller;

import com.yanren.openai00.consumer.SseConsumer;
import com.yanren.openai00.service.MultiRoundsChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/ai/chat-client")
@CrossOrigin
public class OpenAIChatClientController {

    @Autowired
    MultiRoundsChatService multiRoundsChatService;

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

    // 多輪對話--記憶對話內容
    @GetMapping("/multi-rounds-chat")
    String multiRoundsChat(@RequestParam(value = "message") String message) {
        return multiRoundsChatService.chat(message);
    }

    // 映射到實體
    @GetMapping("entity")
    Menu callEntity(){
        return chatClient.prompt()
                .user("以繁體中文隨機生成某間餐廳的五道菜餚名稱")
                .call()
                .entity(Menu.class);
    }

}

record Menu(String restaurant, List<String> dishes){}
