package io.github.magicdgs.gaming.ygoprodeck.client;

public class MockYgoprodeckRetrofitAsyncClientTest extends AbstractMockYgoprodeckClientTest {
    @Override
    protected boolean isAsync() {
        return true;
    }
}
