package io.github.magicdgs.gaming.ygoprodeck.client.retrofit;

public class MockYgoprodeckRetrofitAsyncClientTest extends AbstractMockYgoprodeckRetrofitClientTest {
    @Override
    protected boolean isAsync() {
        return true;
    }
}
