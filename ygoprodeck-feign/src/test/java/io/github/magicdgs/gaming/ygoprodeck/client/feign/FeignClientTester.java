package io.github.magicdgs.gaming.ygoprodeck.client.feign;

import io.github.magicdgs.gaming.ygoprodeck.Constants;
import io.github.magicdgs.gaming.ygoprodeck.api.feign.DatabaseApi;
import io.github.magicdgs.gaming.ygoprodeck.model.*;
import io.github.magicdgs.gaming.ygoprodeck.testutils.DatabaseClientTester;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FeignClientTester implements DatabaseClientTester {

    private final YgoprodeckFeignClient client;

    public static FeignClientTester createTester(final String url, final boolean strict) {
        var client = new YgoprodeckFeignClient.Builder() //
                .strict(strict) //
                .rateLimit(Constants.MAX_REQUEST_PER_SECOND, Duration.of(1, ChronoUnit.SECONDS))
                .commonUrl(url) //
                .build();
        return new FeignClientTester(client);
    }

    @Override
    public List<CheckDBVersionDTO> callGetCheckDbVersion() throws Exception {
        return client.getDatabaseApi().getCheckDBVer();
    }

    @Override
    public List<CardSetItemDTO> callGetCardSets() throws Exception {
        return client.getDatabaseApi().getCardSets();
    }

    @Override
    public List<ArchetypesItemDTO> callGetArchetypes() throws Exception {
        return client.getDatabaseApi().getArchetypes();
    }

    @Override
    public CardSetInfo callGetCardSetInfo(String setCode) throws Exception {
        return client.getDatabaseApi().getCardSetInfo(setCode);
    }

    @Override
    public Card callGetRandomCard() throws Exception {
        return client.getDatabaseApi().getRandomCard();
    }

    @Override
    public CardInfoDTO callGetCardInfo() throws Exception {
        var params = new DatabaseApi.GetCardInfoQueryParams();
        return client.getDatabaseApi().getCardInfo(params);
    }

    @Override
    public CardInfoDTO callGetCardInfoWithMiscParam(YesSwitch yesSwitch) throws Exception {
        var params = new DatabaseApi.GetCardInfoQueryParams() //
                .misc(yesSwitch);
        return client.getDatabaseApi().getCardInfo(params);
    }

    @Override
    public CardInfoDTO callGetCardInfoWithPagination(int num, int offset) throws Exception {
        var params = new DatabaseApi.GetCardInfoQueryParams() //
                .num(num).offset(offset);
        return client.getDatabaseApi().getCardInfo(params);
    }

    @Override
    public void callGetCardInfoWithWrongTypeParam(String wrongType) throws Exception {
        var params = new DatabaseApi.GetCardInfoQueryParams() //
                .type(List.of(wrongType));
        client.getDatabaseApi().getCardInfo(params);
    }
}
