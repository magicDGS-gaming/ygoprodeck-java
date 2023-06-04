package io.github.magicdgs.gaming.ygoprodeck.server.config;

import io.github.magicdgs.gaming.ygoprodeck.client.retrofit.YgoprodeckRetrofitClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.magicdgs.gaming.ygoprodeck.model.json.JsonConverter;

@Configuration
public class YgoprodeckClientConfig {
    @Bean
    public YgoprodeckRetrofitClient clientBean() {
        return new YgoprodeckRetrofitClient.Builder()
                .defaultUrls()
                .build();
    }
    @Bean
    public ObjectMapper objectMapperBean() {
        return JsonConverter.createObjectMapper(false);
    }

}
