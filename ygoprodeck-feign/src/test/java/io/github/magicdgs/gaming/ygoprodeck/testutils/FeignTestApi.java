package io.github.magicdgs.gaming.ygoprodeck.testutils;

import feign.Headers;
import feign.RequestLine;
import feign.Response;
import io.github.magicdgs.gaming.ygoprodeck.model.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public interface FeignTestApi {

	@RequestLine("GET /test")
	@Headers({
			"Accept: application/json",
	})
	Response testWithRawResponse();

	@RequestLine("GET /test")
	@Headers({
			"Accept: application/json",
	})
	ApiResponse<TestResponse> testWithHttpInfo();

	@RequestLine("GET /test")
	@Headers({
			"Accept: application/json",
	})
	TestResponse test();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@SuperBuilder
	public static class TestResponse {
		private String testName;
		private int number;
	}

}
