package io.github.magicdgs.gaming.ygoprodeck.server.config;

import org.dizitart.no2.Nitrite;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Configuration
public class ApplicationConfig {

    public static final String PROXY_PROFILE = "proxy";

    @Bean
    @Profile("!" + PROXY_PROFILE)
    public Nitrite nitriteDb(final ApplicationConfigProperties appConfigProperties) {
        final Path basePath = Paths.get(appConfigProperties.getStorage().getBaseDirectory());
        if (!Files.exists(basePath)) {
            try {
                Files.createDirectories(basePath);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        final String database = basePath.resolve("db.nitrite").toString();
        return Nitrite.builder()
                .filePath(database)
                .openOrCreate();
    }

}
