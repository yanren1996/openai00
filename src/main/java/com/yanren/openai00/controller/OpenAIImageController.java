package com.yanren.openai00.controller;

import com.yanren.openai00.config.HttpClientConfig;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/ai/image")
public class OpenAIImageController {

    @Autowired
    ImageModel imageModel;

    @Autowired
    ChatModel chatModel;

    @Autowired
    HttpClientConfig.ImgHttpClient imgHttpClient;

    @GetMapping("/generate")
    public String imageGenerate(String message) throws IOException {
        ImageResponse response = imageModel.call(new ImagePrompt(message));
        String uri = response.getResult().getOutput().getUrl();
        // http請求發送
        byte[] img = imgHttpClient.getImage(URI.create(uri));
        // 先把圖片存到server免得過期
        String imgPath = "img/" + UUID.randomUUID() + ".png";
        Files.write(Path.of("./target/classes/static/" + imgPath), img);

        return imgPath;
    }

}
