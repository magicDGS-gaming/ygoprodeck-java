package io.github.magicdgs.gaming.ygoprodeck.api.feign;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.Types;
import feign.jackson.JacksonDecoder;
import io.github.magicdgs.gaming.ygoprodeck.model.ApiResponse;
import io.github.magicdgs.gaming.ygoprodeck.model.exception.YgoprodeckException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class YgoprodeckApiResponseDecoder extends ApiResponseDecoder {

    public YgoprodeckApiResponseDecoder(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        try {
            return super.decode(response, type);
        } catch (final Exception e) {
            if (e instanceof JacksonException) {
                // TODO: maybe create specific error for this to store the model?
                throw new YgoprodeckException("Invalid model", e);
            }
            throw e;
        }
    }
}
