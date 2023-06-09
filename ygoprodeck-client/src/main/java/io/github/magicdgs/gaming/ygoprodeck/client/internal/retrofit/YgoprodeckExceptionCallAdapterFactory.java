package io.github.magicdgs.gaming.ygoprodeck.client.internal.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import io.github.magicdgs.gaming.ygoprodeck.model.exception.YgoprodeckException;

/**
 * CallAdapter factory to be used by retrofit on this library.
 * </br>
 * This CallAdapter factory ensures that API errors are properly thrown as
 * {@link YgoprodeckException}s
 * if they are known by the library. 
 */
public class YgoprodeckExceptionCallAdapterFactory extends CallAdapter.Factory {

	public static CallAdapter.Factory create() {
        return new YgoprodeckExceptionCallAdapterFactory();
    }

    @Override
    public @Nullable CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
      if (getRawType(returnType) != Call.class) {
        return null;
      }
      if (!(returnType instanceof ParameterizedType)) {
        throw new IllegalStateException("Call must have generic type (e.g., Call<ResponseBody>)");
      }
      final Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
      final Executor callbackExecutor = retrofit.callbackExecutor();
      return new ExceptionCallAdapter<>(responseType, callbackExecutor);
    }
    
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ExceptionCallAdapter<R> implements CallAdapter<R, Call<R>> {
      private final Type responseType;
      private final Executor callbackExecutor;

      @Override
      public Type responseType() {
        return responseType;
      }

      @Override
      public Call<R> adapt(Call<R> call) {
        return new YgoprodeckCallWrapper<>(call, callbackExecutor);
      }
    }
	
}