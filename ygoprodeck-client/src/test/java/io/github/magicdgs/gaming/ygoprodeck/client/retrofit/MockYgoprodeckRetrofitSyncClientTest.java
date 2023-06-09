package io.github.magicdgs.gaming.ygoprodeck.client.retrofit;

public class MockYgoprodeckRetrofitSyncClientTest extends AbstractMockYgoprodeckRetrofitClientTest {
    @Override
    protected boolean isAsync() {
        return false;
    }
}
