package io.github.magicdgs.gaming.ygoprodeck.server.image.impl;

import io.github.magicdgs.gaming.ygoprodeck.client.retrofit.YgoprodeckRetrofitClient;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfig;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfigProperties;
import io.github.magicdgs.gaming.ygoprodeck.server.image.ImageService;
import lombok.AllArgsConstructor;
import okhttp3.ResponseBody;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import retrofit2.Response;

@Service
@AllArgsConstructor
@Profile(ApplicationConfig.PROXY_PROFILE)
public class ProxyImageService implements ImageService {

    private final YgoprodeckRetrofitClient client;

    private ResponseEntity<Resource> toResource(final Response<ResponseBody> response) {
        final HttpHeaders headers = new HttpHeaders();
        response.headers().forEach(pair -> {
            headers.add(pair.getFirst(), pair.getSecond());
        });
        return ResponseEntity.ok()
                .headers(headers)
                .body (new InputStreamResource(response.body().byteStream()));
    }

    @Override
    public ResponseEntity<Resource> getCardImage(Long id) throws Exception {
        final var response = client.getImagesApi().getCardImage(id).execute();
        return toResource(response);
    }

    @Override
    public ResponseEntity<Resource> getCardSmallImage(Long id) throws Exception {
        final var response = client.getImagesApi().getCardSmallImage(id).execute();
        return toResource(response);
    }

    @Override
    public ResponseEntity<Resource> getCardCroppedImage(Long id) throws Exception {
        final var response = client.getImagesApi().getCardCroppedImage(id).execute();
        return toResource(response);
    }

    @Override
    public ResponseEntity<Resource> getSetImage(String setCode) throws Exception {
        final var response = client.getImagesApi().getSetImage(setCode).execute();
        return toResource(response);
    }
}
