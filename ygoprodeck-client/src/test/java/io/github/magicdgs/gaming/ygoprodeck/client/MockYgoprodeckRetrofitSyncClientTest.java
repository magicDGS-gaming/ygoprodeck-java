package io.github.magicdgs.gaming.ygoprodeck.client;

public class MockYgoprodeckRetrofitSyncClientTest extends AbstractMockYgoprodeckClientTest {
    @Override
    protected boolean isAsync() {
        return false;
    }
}
