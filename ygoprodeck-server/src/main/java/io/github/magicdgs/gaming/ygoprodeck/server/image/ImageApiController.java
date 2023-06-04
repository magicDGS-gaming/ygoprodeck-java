package io.github.magicdgs.gaming.ygoprodeck.server.image;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.magicdgs.gaming.ygoprodeck.server.ImagesApi;
import io.github.magicdgs.gaming.ygoprodeck.server.ImagesApiDelegate;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class ImageApiController implements ImagesApi {

    private final ImageService service;

    @Override
    public ImagesApiDelegate getDelegate() {
        return service;
    }

}
