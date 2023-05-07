package io.github.magicdgs.gaming.ygoprodeck.api.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.github.magicdgs.gaming.ygoprodeck.model.ErrorDTO;
import io.github.magicdgs.gaming.ygoprodeck.model.exception.YgoprodeckException;
import io.github.magicdgs.gaming.ygoprodeck.model.exception.YgoprodeckResponseErrorException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
@Slf4j
public class YgoprodeckErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                final ErrorDTO errorModel = parseErrorMessage(response.body());
                if (errorModel != null) {
                    return new YgoprodeckResponseErrorException(errorModel);
                }
            default:
                return defaultDecoder.decode(methodKey, response);
        }
    }

    private ErrorDTO parseErrorMessage(final Response.Body body) {
        try (final InputStream bodyIs = body.asInputStream()) {
            return objectMapper.readValue(bodyIs, ErrorDTO.class);
        } catch (final IOException e) {
            log.error("Cannot parse error message", e);
            return null;
        }
    }
}
