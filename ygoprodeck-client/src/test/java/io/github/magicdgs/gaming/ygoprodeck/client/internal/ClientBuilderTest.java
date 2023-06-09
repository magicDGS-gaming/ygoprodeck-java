package io.github.magicdgs.gaming.ygoprodeck.client.internal;

import io.github.magicdgs.gaming.ygoprodeck.client.exception.YgoprodeckException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class ClientBuilderTest {

    private ClientBuilder<String> testBuilder;

    @BeforeEach
    void beforeEach() {
        testBuilder = new ClientBuilder<String>() {
            @Override
            protected String buildInstance() throws YgoprodeckException {
                return "Built: " + this.toString();
            }
        };
    }

    @Test
    public void testBuildFailsWithNoDatabaseUrl() {
        assertThrows(YgoprodeckException.class,
                () -> testBuilder.imagesUrl("http://localhost:8080/v7/api").build());
    }

    @Test
    public void testBuildFailsWithNoImagesUrl() {
        assertThrows(YgoprodeckException.class,
                () -> testBuilder.dbUrl("http://localhost:8080/v7/api").build());
    }

    @Test
    public void testBuildFailsWithEmptyBuilder() {
        assertThrows(YgoprodeckException.class, () -> testBuilder.build());
    }

    @Test
    public void testNotFailedWithDefaultUrls() {
        Assertions.assertDoesNotThrow(() -> testBuilder.defaultUrls().build());
    }

}