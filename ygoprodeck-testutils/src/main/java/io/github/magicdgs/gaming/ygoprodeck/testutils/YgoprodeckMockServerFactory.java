package io.github.magicdgs.gaming.ygoprodeck.testutils;

import io.github.magicdgs.gaming.ygoprodeck.model.YesSwitch;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.magicdgs.gaming.ygoprodeck.api.DatabaseApi.*;
import static io.github.magicdgs.gaming.ygoprodeck.testutils.YgoprodeckFilesResources.*;

/**
 * Mock server factory for the YGOPRODeck API using the bundled resources.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class YgoprodeckMockServerFactory {

    /**
     * Creates a fresh MockWebServer with a dispatcher for the
     * resources on {@link  YgoprodeckFilesResources} on the paths
     * that should provide them.
     *
     * @return mocked web server.
     */
    public static MockWebServer createMockWebServer() {
        final MockWebServer webServer = new MockWebServer();
        // TODO: maybe we should enable some extension of this for testing on a different project
        // TODO: for example, if they want to check the LocalDateTime version of the DB
        webServer.setDispatcher(new YgoprodeckDispatcher());
        return webServer;
    }

    private static class YgoprodeckDispatcher extends Dispatcher {

        @NotNull
        @Override
        public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
            final HttpUrl requestUrl = recordedRequest.getRequestUrl();
            return switch (requestUrl.pathSegments().get(0)) {
                case GET_CARD_INFO_PATH -> cardInfoResponse(requestUrl);
                case GET_CHECK_DB_VER_PATH -> createMockResponseFromResource(CHECKDBVER_RESOURCE, 200);
                case GET_ARCHETYPES_PATH -> createMockResponseFromResource(ARCHETYPES_RESOURCE, 200);
                case GET_CARD_SETS_PATH -> createMockResponseFromResource(CARDSETS_RESOURCE, 200);
                case GET_CARD_SET_INFO_PATH -> cardSetInfoResponse(requestUrl);
                case GET_RANDOM_CARD_PATH -> createMockResponseFromResource(RANDOMCARD_RESOURCE, 200);
                default -> throw new IllegalStateException("Request path is not mocked: " + requestUrl);
            };
        }

        private MockResponse cardInfoResponse(final HttpUrl url) {
            // no params -> all cards mock
            if (url.querySize() == 0) {
                return createMockResponseFromResource(CARDINFO_RESOURCE, 200);
            }
            // misc param -> all cards with misc mock
            if (url.querySize() == 1
                    && checkParamValue(url, GetCardInfoQueryMap.MISC, YesSwitch.YES)
            ) {
                return createMockResponseFromResource(CARDINFO_MISC_RESOURCE, 200);
            }
            // num + offset -> paginated mock
            if (url.querySize() == 2
                    && checkParamValue(url, GetCardInfoQueryMap.NUM, CARDINFO_PAGINATED_NUM_PARAM)
                    && checkParamValue(url, GetCardInfoQueryMap.OFFSET, CARDINFO_PAGINATED_OFFSET_PARAM)
            ) {
                return createMockResponseFromResource(CARDINFO_PAGINATED_RESOURCE, 200);
            }
            // type=wrong -> type error mock
            if (url.querySize() == 1
                    && checkParamValue(url, GetCardInfoQueryMap.TYPE, CARDINFO_TYPE_ERROR_PARAM)
            ) {
                return createMockResponseFromResource(CARDINFO_TYPE_ERROR_RESOURCE, 400);
            }
            throw new IllegalStateException(GET_CARD_INFO_PATH + " request not mocked for params: " + url);
        }

        private boolean checkParamValue(final HttpUrl url, final String param, final Object expectedValue) {
            final String queryParameter = url.queryParameter(param);
            if (queryParameter != null && queryParameter.equals(expectedValue.toString())) {
                return true;
            }
            return false;
        }

        private MockResponse cardSetInfoResponse(final HttpUrl url) {
            // setcod=valid -> set code mock
            if (url.querySize() == 1
                    && checkParamValue(url, GetCardSetInfoQueryMap.SETCODE, CARDSETINFO_SETCODE_PARAM)
            ) {
                return createMockResponseFromResource(CARDSETINFO_SETCODE_RESOURCE, 200);
            }
            throw new IllegalStateException(GET_CARD_SET_INFO_PATH + " request is not mocked: " + url);
        }

        public MockResponse createMockResponseFromResource(final String resourceName, final int responseCode) {
            final Path resourcePath = YgoprodeckFilesResources.getResource(resourceName);
            try {
                final String resourceString = Files.readString(resourcePath);
                return new MockResponse()
                        .setBody(resourceString)
                        .setResponseCode(responseCode);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read resource: " + resourcePath, e);
            }
        }
    }

}
