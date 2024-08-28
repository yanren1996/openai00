package com.yanren.openai00.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MultiRoundsChatService {
    private final ChatClient chatClient;
    private List<Message> messages = new ArrayList<>();

    public MultiRoundsChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    private void addUserMessage(String message){
        Message userMessage = new UserMessage(message);
        messages.add(userMessage);
    }

    private void addAssistantMessage(String message){
        Message assistantMessage = new AssistantMessage(message);
        messages.add(assistantMessage);
    }

    public String chat(String message){
        if (message.equalsIgnoreCase("reset")) {
            messages = new ArrayList<>();
            return "Chat Reset.";
        }

        addUserMessage(message);
        String response = chatClient.prompt(new Prompt(messages)).call().content();
        addAssistantMessage(response);
        return response;
    }
}
