package io.github.magicdgs.gaming.ygoprodeck.client.internal.retrofit;

import java.util.concurrent.Executor;

import io.github.magicdgs.gaming.ygoprodeck.model.ErrorDTO;
import io.github.magicdgs.gaming.ygoprodeck.client.exception.YgoprodeckException;
import io.github.magicdgs.gaming.ygoprodeck.client.exception.YgoprodeckResponseErrorException;
import io.github.magicdgs.gaming.ygoprodeck.model.json.JsonConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
class YgoprodeckExceptionCallback<T> implements Callback<T> {
    private final Executor callbackExecutor;
    private final Callback<T> delegate;

	@Override
	public void onResponse(Call<T> call, Response<T> response) {
		if (response.isSuccessful()) {
            execute(() -> delegate.onResponse(call, response));
        } else {
        	execute(() -> delegate.onFailure(call, errorToBodyToThrowable(response.errorBody())));
        }
	}

	private Throwable errorToBodyToThrowable(ResponseBody body) {
		try {
			// TODO: maybe we can do better because it might be not extrict/lenient here...
			return new YgoprodeckResponseErrorException(JsonConverter.toObject(body.string(), ErrorDTO.class));
		} catch (final Exception e) {
			return new YgoprodeckException("Unexpected error format", e);
		}
		
	}
	
	@Override
	public void onFailure(Call<T> call, Throwable t) {
        final YgoprodeckException finalException = new YgoprodeckException("Unexpected exception", t);
        execute(() -> delegate.onFailure(call, finalException));
	}
	
	private void execute(final Runnable action) {
		if (callbackExecutor == null) {
			action.run();
		} else {
			callbackExecutor.execute(action);
		}
	}
}
