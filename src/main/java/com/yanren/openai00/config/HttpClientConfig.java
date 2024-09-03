package com.yanren.openai00.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.URI;

@Configuration
public class HttpClientConfig {

    @Bean
    ImgHttpClient imgHttpClient() {
        WebClient client = WebClient.builder()
                // openai圖檔太大了將緩衝擴充到10M
                .codecs(config -> {
                    config.defaultCodecs().maxInMemorySize(10 * 1024 *1024);
                })
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(ImgHttpClient.class);
    }

    public interface ImgHttpClient {
        /**
         * @param uri 直接傳入完整uri
         * @return openai生成的二進制圖片
         */
        @GetExchange
        byte[] getImage(URI uri);
    }
}
