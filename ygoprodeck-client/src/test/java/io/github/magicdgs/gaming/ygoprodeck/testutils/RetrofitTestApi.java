package io.github.magicdgs.gaming.ygoprodeck.testutils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitTestApi {
	
    @GET("/test")
    Call<TestResponse> test();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@SuperBuilder
	class TestResponse {
		private String testName;
		private int number;
	}

}
