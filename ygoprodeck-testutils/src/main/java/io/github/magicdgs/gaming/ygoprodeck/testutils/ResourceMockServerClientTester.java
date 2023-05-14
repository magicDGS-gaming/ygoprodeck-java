package io.github.magicdgs.gaming.ygoprodeck.testutils;

import io.github.magicdgs.gaming.ygoprodeck.model.*;
import io.github.magicdgs.gaming.ygoprodeck.model.json.JsonConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static io.github.magicdgs.gaming.ygoprodeck.utils.DatabaseApiQueryUtils.*;

@RequiredArgsConstructor
public final class ResourceMockServerClientTester implements DatabaseClientTester {

    // mock web server
    @NonNull
    private final MockWebServer mockWebServer;
    @NonNull
    private final DatabaseClientTester delegate;

    @Override
    public List<CheckDBVersionDTO> callGetCheckDbVersion() throws Exception {
        enqueueSuccessResource(YgoprodeckFilesResources.CHECKDBVER_RESOURCE);
        return delegate.callGetCheckDbVersion();
    }

    @Override
    public List<CardSetItemDTO> callGetCardSets() throws Exception {
        enqueueSuccessResource(YgoprodeckFilesResources.CARDSETS_RESOURCE);
        return delegate.callGetCardSets();
    }

    @Override
    public List<ArchetypesItemDTO> callGetArchetypes() throws Exception {
        enqueueSuccessResource(YgoprodeckFilesResources.ARCHETYPES_RESOURCE);
        return delegate.callGetArchetypes();
    }

    @Override
    public CardSetInfo callGetCardSetInfo(GetCardSetInfoQueryParams params) throws Exception {
        enqueueSuccessResource(YgoprodeckFilesResources.CARDSETINFO_SETCODE_RESOURCE);
        return delegate.callGetCardSetInfo(params);
    }

    @Override
    public Card callGetRandomCard() throws Exception {
        enqueueSuccessResource(YgoprodeckFilesResources.RANDOMCARD_RESOURCE);
        return delegate.callGetRandomCard();
    }

    @Override
    public CardInfoDTO callGetCardInfo(GetCardInfoQueryParams params) throws Exception {
        enqueueSuccessResource(YgoprodeckFilesResources.CARDINFO_RESOURCE);
        return delegate.callGetCardInfo(params);
    }

    @Override
    public CardInfoDTO callGetCardInfoWithMiscParam(GetCardInfoQueryParams params) throws Exception {
        enqueueSuccessResource(YgoprodeckFilesResources.CARDINFO_MISC_RESOURCE);
        return delegate.callGetCardInfoWithMiscParam(params);
    }

    @Override
    public CardInfoDTO callGetCardInfoWithPagination(GetCardInfoQueryParams params) throws Exception {
        enqueueSuccessResource(YgoprodeckFilesResources.CARDINFO_PAGINATED_RESOURCE);
        return delegate.callGetCardInfoWithPagination(params);
    }

    @Override
    public void callGetCardInfoWithWrongTypeParam(GetCardInfoQueryParams params) throws Exception {
        enqueueErrorResource(YgoprodeckFilesResources.CARDINFO_TYPE_ERROR_RESOURCE);
        delegate.callGetCardInfoWithWrongTypeParam(params);
    }

    public void enqueueErrorResource(final String resourceName) throws Exception {
        enqueueResource(resourceName, 400);
    }

    public void enqueueSuccessResource(final String resourceName) throws Exception {
        enqueueResource(resourceName, 200);
    }

    public void enqueueResource(final String resourceName, final int responseCode) throws Exception {
        final Path resourcePath = YgoprodeckFilesResources.getResource(resourceName);
        final String resourceString = Files.readString(resourcePath);
        final MockResponse mock = new MockResponse().setBody(resourceString).setResponseCode(responseCode);
        mockWebServer.enqueue(mock);
    }

    public <T> void enqueueResponseWithCode(final int responseCode, final T response) throws Exception {
        final String asString = JsonConverter.asJson(response);
        final MockResponse mock = new MockResponse().setBody(asString).setResponseCode(responseCode);
        mockWebServer.enqueue(mock);
    }

    public void enqueueError(final ErrorDTO response) throws Exception {
        enqueueResponseWithCode(400, response);
    }

    public <T> void enqueueSuccessResponse(final T response) throws Exception {
        enqueueResponseWithCode(200, response);
    }
}
