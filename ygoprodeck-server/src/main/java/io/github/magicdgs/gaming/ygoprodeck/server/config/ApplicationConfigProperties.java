package io.github.magicdgs.gaming.ygoprodeck.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ygoprodeck.server")
public class ApplicationConfigProperties {
    private final Storage storage;

    private final Db db;

    @Data
    public static class Storage {
        private final String baseDirectory;

    }

    @Data
    public static class Db {
        private final boolean forceReload;
    }

}
