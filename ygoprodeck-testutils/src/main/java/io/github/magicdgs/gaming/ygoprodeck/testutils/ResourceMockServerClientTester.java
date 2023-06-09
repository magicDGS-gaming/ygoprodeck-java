package io.github.magicdgs.gaming.ygoprodeck.testutils;

import io.github.magicdgs.gaming.ygoprodeck.model.*;
import io.github.magicdgs.gaming.ygoprodeck.model.json.JsonConverter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import static io.github.magicdgs.gaming.ygoprodeck.utils.DatabaseApiQueryUtils.*;

public final class ResourceMockServerClientTester implements DatabaseClientTester, Closeable {

    // mock web server
    private final MockWebServer mockWebServer;
    private final DatabaseClientTester delegate;

    public ResourceMockServerClientTester(Function<String, DatabaseClientTester> urlToClientTester) {
        mockWebServer = YgoprodeckMockServerFactory.createMockWebServer();
        this.delegate = urlToClientTester.apply(mockWebServer.url("/").toString());
    }

    @Override
    public List<CheckDBVersionDTO> callGetCheckDbVersion() throws Exception {
        return delegate.callGetCheckDbVersion();
    }

    @Override
    public List<CardSetItemDTO> callGetCardSets() throws Exception {
        return delegate.callGetCardSets();
    }

    @Override
    public List<ArchetypesItemDTO> callGetArchetypes() throws Exception {
        return delegate.callGetArchetypes();
    }

    @Override
    public CardSetInfo callGetCardSetInfo(GetCardSetInfoQueryParams params) throws Exception {
        return delegate.callGetCardSetInfo(params);
    }

    @Override
    public Card callGetRandomCard() throws Exception {
        return delegate.callGetRandomCard();
    }

    @Override
    public CardInfoDTO callGetCardInfo(GetCardInfoQueryParams params) throws Exception {
        return delegate.callGetCardInfo(params);
    }

    @Override
    public CardInfoDTO callGetCardInfoWithMiscParam(GetCardInfoQueryParams params) throws Exception {
        return delegate.callGetCardInfoWithMiscParam(params);
    }

    @Override
    public CardInfoDTO callGetCardInfoWithPagination(GetCardInfoQueryParams params) throws Exception {
        return delegate.callGetCardInfoWithPagination(params);
    }

    @Override
    public void callGetCardInfoWithWrongTypeParam(GetCardInfoQueryParams params) throws Exception {
        delegate.callGetCardInfoWithWrongTypeParam(params);
    }

    @Override
    public void close() throws IOException {
        mockWebServer.close();
    }
}
