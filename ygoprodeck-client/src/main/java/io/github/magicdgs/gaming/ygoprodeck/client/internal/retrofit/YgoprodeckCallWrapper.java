package io.github.magicdgs.gaming.ygoprodeck.client.internal.retrofit;

import java.io.IOException;
import java.util.concurrent.Executor;

import io.github.magicdgs.gaming.ygoprodeck.client.YgoprodeckResultCallback;
import io.github.magicdgs.gaming.ygoprodeck.model.exception.YgoprodeckResponseErrorException;
import lombok.AllArgsConstructor;
import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AllArgsConstructor
class YgoprodeckCallWrapper<T> implements Call<T> {
    private final Call<T> delegate;
    private final Executor callbackExecutor;

	@Override
	public void enqueue(Callback<T> callback) {
		delegate.enqueue(new YgoprodeckExceptionCallback<>(callbackExecutor, callback));
	}
    
	@Override
	public Response<T> execute() throws IOException {
		final YgoprodeckResultCallback<T> resultCallback = new YgoprodeckResultCallback<>();
		// do not use the callback executor
		delegate.enqueue(new YgoprodeckExceptionCallback<>(callbackExecutor, resultCallback));
		try {
			resultCallback.awaitResult();
		} catch (final InterruptedException e) {
			throw new IOException(e);
		}
		return resultCallback.getResponse().orElseThrow(//
				() -> new YgoprodeckResponseErrorException(resultCallback.getError().get()));
	}

	@Override
	public boolean isExecuted() {
		return delegate.isExecuted();
	}

	@Override
	public void cancel() {
		delegate.cancel();
	}

	@Override
	public boolean isCanceled() {
		return delegate.isCanceled();
	}

	@Override
	public Request request() {
		return delegate.request();
	}

	@Override
	public Timeout timeout() {
		return delegate.timeout();
	}
	
    @SuppressWarnings("CloneDoesntCallSuperClone") // Performing deep clone.
    @Override
    public Call<T> clone() {
        return new YgoprodeckCallWrapper<>(delegate.clone(), callbackExecutor);
    }
}