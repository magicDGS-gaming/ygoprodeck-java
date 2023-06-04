package io.github.magicdgs.gaming.ygoprodeck.server;

import io.github.magicdgs.gaming.ygoprodeck.server.common.DatabaseReloader;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfig;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfigProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@Profile("!" + ApplicationConfig.PROXY_PROFILE)
public class LoadDatabaseRunner implements ApplicationRunner {

    private final ApplicationConfigProperties config;
    private final DatabaseReloader databaseReloader;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        databaseReloader.reloadDatabase(config.getDb().isForceReload());
    }

}
