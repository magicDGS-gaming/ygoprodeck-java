package io.github.magicdgs.gaming.ygoprodeck.server.database.utils;

import io.github.magicdgs.gaming.ygoprodeck.model.Card;
import io.github.magicdgs.gaming.ygoprodeck.model.CardInfoDTO;
import io.github.magicdgs.gaming.ygoprodeck.model.CardSetItemDTO;
import io.github.magicdgs.gaming.ygoprodeck.model.Images;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlReplacer {

    public static Optional<UriComponents> getServerUriComponents() {
        try {
            return Optional.of(ServletUriComponentsBuilder.fromCurrentRequest().build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static final CardSetItemDTO replaceUrl(final CardSetItemDTO cardSet, final UriComponents serverUriComponents) {
        final String newSetImage = toAppUrl(
                cardSet.getSetImage(),
                serverUriComponents);
        cardSet.setSetImage(newSetImage);
        return cardSet;
    }

    public static Card replaceUrl(final Card card,
                                final UriComponents serverUriComponents) {
        final List<Images> cardImages = card.getCardImages().stream()
                .map(originalImgs -> {
                    final Images newImgs = new Images();
                    newImgs.setImageUrl(toAppUrl(
                            originalImgs.getImageUrl(), serverUriComponents));
                    newImgs.setImageUrlCropped(toAppUrl(
                            originalImgs.getImageUrlCropped(), serverUriComponents));
                    newImgs.setImageUrlSmall(toAppUrl(
                            originalImgs.getImageUrlSmall(), serverUriComponents));
                    return newImgs;
                })
                .toList();
        card.setCardImages(cardImages);
        return card;
    }

    private static String toAppUrl(final String httpUrl,
                            final UriComponents serverUriComponents) {
        if (httpUrl == null) {
            return null;
        }
        return UriComponentsBuilder.fromHttpUrl(httpUrl)
                .scheme(serverUriComponents.getScheme())
                .host(serverUriComponents.getHost())
                .port(serverUriComponents.getPort())
                .build()
                .toUriString();
    }

}
