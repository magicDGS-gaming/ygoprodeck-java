package io.github.magicdgs.gaming.ygoprodeck.client;

import io.github.magicdgs.gaming.ygoprodeck.client.exception.YgoprodeckException;
import io.github.magicdgs.gaming.ygoprodeck.client.exception.YgoprodeckResponseErrorException;
import io.github.magicdgs.gaming.ygoprodeck.model.ErrorDTO;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * Callback to be used by the library to wait for the result and/or get the error model if it fails.
 *
 * @param <T> the expected result type of the callback
 */
public final class YgoprodeckResultCallback<T> implements Callback<T> {

	private final CountDownLatch countdown = new CountDownLatch(1);
	
	private Response<T> response = null;
	private ErrorDTO errorDto  = null;
	private Throwable failure;
	
	/**
	 * Awaits for the result.
	 * 
	 * @throws InterruptedException
	 */
	public void awaitResult() throws InterruptedException {
		countdown.await();
	}

	/**
	 * Gets the result if any.
	 * </br>
	 * NOTE: the result might be empty if the callback was not yet executed
	 * or if an error have occurred.
	 * 
	 * @return optional result; {@code Optional#empty()} if none.
	 */
	public Optional<T> getResult() {
		if (response == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(response.body());
	}

	/**
	 * Gets the full response if any.
	 * </br>
	 * NOTE: the response might be empty if the callback was not yet executed
	 * or if an error have occurred.
	 * 
	 * @return optional result; {@code Optional#empty()} if none.
	 */
	public Optional<Response<T>> getResponse() {
		return Optional.ofNullable(response);
	}

	/**
	 * Gets the original failure (if any) from the client call.
	 * </br>
	 * NOTE: the failure might be empty if the callback was not yet executed
	 * or if a successful answer have occurred.
	 *
	 * @return optional error; {@code Optional#empty()} if none.
	 */
	public Optional<Throwable> getFailure() {
		return Optional.ofNullable(failure);
	}
	
	/**
	 * Gets the error if any.
	 * </br>
	 * NOTE: the error might be empty if the callback was not yet executed
	 * or if a successful answer have occurred.
	 * 
	 * @return optional error; {@code Optional#empty()} if none.
	 */
	public Optional<ErrorDTO> getError() {
		return Optional.ofNullable(errorDto);
	}

	@Override
	public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
		this.response = response;
		countdown.countDown();
	}

	@Override
	public void onFailure(@NotNull Call<T> call, @NotNull Throwable t) {
		failure = t;
		if (t instanceof YgoprodeckResponseErrorException) {
			this.errorDto = ((YgoprodeckResponseErrorException) t).getError();
		} else {
			this.errorDto = new ErrorDTO();
			final Throwable cause = (t instanceof YgoprodeckException) ? t.getCause() : t;
			this.errorDto.setError(cause.getMessage());
		}
		countdown.countDown();
	}
	
}
