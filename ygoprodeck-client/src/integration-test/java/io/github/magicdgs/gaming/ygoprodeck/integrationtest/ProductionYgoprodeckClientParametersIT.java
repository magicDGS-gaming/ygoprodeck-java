package io.github.magicdgs.gaming.ygoprodeck.integrationtest;

import io.github.magicdgs.gaming.ygoprodeck.client.YgoprodeckClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static io.github.magicdgs.gaming.ygoprodeck.api.DatabaseApi.GetCardInfoQueryMap;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ProductionYgoprodeckClientParametersIT {

    private static YgoprodeckClient TEST_CLIENT;

    @BeforeAll
    public static void beforeAll() throws Exception {
        log.atDebug().log("Waiting 5 seconds to start the tests");
        TimeUnit.SECONDS.sleep(5);
        TEST_CLIENT = ProductionYgoprodeckClientInstance.getInstance();
    }

    @Test
    public void testGetCardinfoWithDateParams() throws Exception {
        var query = new GetCardInfoQueryMap() //
                .startdate(LocalDate.of(2000, 1, 1))//
                .enddate(LocalDate.of(2004, 1, 1)) //
                // limit the output not to stress the API
                .num(1).offset(0);
        var result = TEST_CLIENT.getDatabaseApi().getCardInfo(query).execute();
        // TODO: we should also test the tcgDate -> we should also ensure that the model is correct on that regard!
        assertAll(
                () -> assertEquals(200, result.code()),
                () -> assertEquals(1, result.body().getData().size())
        );
    }
}
