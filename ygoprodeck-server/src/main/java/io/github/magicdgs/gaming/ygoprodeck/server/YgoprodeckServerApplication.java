package io.github.magicdgs.gaming.ygoprodeck.server;

import io.github.magicdgs.gaming.ygoprodeck.server.common.DatabaseReloader;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
        ApplicationConfigProperties.class
})
@SpringBootApplication
public class YgoprodeckServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YgoprodeckServerApplication.class, args);
    }
}
