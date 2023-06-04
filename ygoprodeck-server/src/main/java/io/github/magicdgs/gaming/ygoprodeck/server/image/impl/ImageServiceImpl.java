package io.github.magicdgs.gaming.ygoprodeck.server.image.impl;

import io.github.magicdgs.gaming.ygoprodeck.api.retrofit.ImagesApi;
import io.github.magicdgs.gaming.ygoprodeck.client.retrofit.YgoprodeckRetrofitClient;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfig;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfigProperties;
import io.github.magicdgs.gaming.ygoprodeck.server.image.Image;
import io.github.magicdgs.gaming.ygoprodeck.server.image.ImageRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.image.ImageService;
import lombok.AllArgsConstructor;
import okhttp3.ResponseBody;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Profile("!" + ApplicationConfig.PROXY_PROFILE)
public class ImageServiceImpl implements ImageService {
    private final YgoprodeckRetrofitClient client;
    private final ImageRepository imageRepository;

    @Override
    public ResponseEntity<Resource> getCardImage(final Long id) {
        return loadOrFetchImage(Long.toString(id), "cards",
                api -> api.getCardImage(id));
    }

    @Override
    public ResponseEntity<Resource> getCardCroppedImage(Long id) throws Exception {
        return loadOrFetchImage(Long.toString(id), "cards_cropped",
                api -> api.getCardCroppedImage(id));
    }

    @Override
    public ResponseEntity<Resource> getCardSmallImage(Long id) throws Exception {
        return loadOrFetchImage(Long.toString(id), "cards",
                api -> api.getCardSmallImage(id));
    }

    @Override
    public ResponseEntity<Resource> getSetImage(String setCode) throws Exception {
        return loadOrFetchImage(setCode, "sets",
                api -> api.getSetImage(setCode));
    }

    private ResponseEntity<Resource> loadOrFetchImage(final String name, String type,
                                                      final Function<ImagesApi, Call<ResponseBody>> fetchFunction) {
        final String id = imageRepository.formatId(type, name);
        final Image loaded;
        if (imageRepository.existsById(id)) {
            // TODO: better error handling
            loaded = imageRepository.findById(id).orElseThrow();
        } else {
            try {
                // fetch
                Response<ResponseBody> response = fetchFunction.apply(client.getImagesApi()).execute();
                // save
                final byte[] content = response.body().bytes();
                final Image image = Image.builder() //
                        .name(name)
                        .type(type)
                        .content(new ByteArrayResource(content))
                        .build();
                loaded = imageRepository.save(image);
            } catch (final IOException e) {
                // TODO: better error handling
                throw new RuntimeException(e);
            }
        }
        // TODO: also set other headers??
        return ResponseEntity.ok() //
                .header(HttpHeaders.CONTENT_TYPE, "image/jpg") //
                .body(loaded.getContent());
    }


}
