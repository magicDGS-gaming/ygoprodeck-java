package io.github.magicdgs.gaming.ygoprodeck.server.image.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfig;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import io.github.magicdgs.gaming.ygoprodeck.server.image.Image;
import io.github.magicdgs.gaming.ygoprodeck.server.image.ImageRepository;

import javax.annotation.PostConstruct;


// TODO: evaluate https://paulcwarren.github.io/spring-content/ to serve/store from different providers
@Repository
@RequiredArgsConstructor
@Profile("!" + ApplicationConfig.PROXY_PROFILE)
public class FileSystemImageRepository implements ImageRepository {

    private final ApplicationConfigProperties appConfig;

    private Path basePath;

    @PostConstruct
    private void createBaseFolder() {
        this.basePath =  Paths.get(appConfig.getStorage().getBaseDirectory(), //
                "images");
        try {
            Files.createDirectories(this.basePath);
        } catch (final IOException e) {
            // TODO: better error handling
            throw new RuntimeException(e);
        }
    }

    @Override
    public String formatId(final String name, final String type) {
        return name + File.separator + name;
    }

    private Path getImageFile(final String id) {
        return basePath.resolve(id + ".jpg");
    }

    private Path getImageFile(final String name, final String type) {
        return basePath.resolve(formatId(name, type) + ".jpg");
    }


    private Image toImage(final String name, final String type, final Resource resource) {
        return Image.builder() //
                .name(name) //
                .type(type) //
                .content(resource) //
                .build();
    }

    @Override
    public <S extends Image> S save(S entity) {
        if (entity.getId() != null) {
            throw new IllegalArgumentException("Cannot store image with already set ID");
        }
        if (entity.getContent() == null) {
            throw new IllegalArgumentException("Cannot store empty image");
        }
        try {
            final String id = formatId(entity.getName(), entity.getType());
            final Path path =  getImageFile(id);
            Files.createDirectories(path.getParent());
            Files.write(path, entity.getContent().getContentAsByteArray());
            entity.setId(id);
            return entity;
        } catch (final IOException e) {
            // TODO: better error handling
            throw new RuntimeException(e);
        }
    }

    @Override
    public <S extends Image> List<S> saveAll(Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), false) //
                .map(entity -> save(entity))
                .toList();
    }

    @Override
    public Optional<Image> findById(String s) {
        Path path = getImageFile(s);
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        final String[] typeAndName = s.split(File.separator);
        final Image img = Image.builder() //
                .name(typeAndName[0]) //
                .type(typeAndName[1]) //
                .content(new FileSystemResource(path)) //
                .build();
        return Optional.of(img);
    }

    @Override
    public boolean existsById(String s) {
        return Files.exists(getImageFile(s));
    }

    @Override
    public List<Image> findAll() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Image> findAllById(Iterable<String> strings) {
        return StreamSupport.stream(strings.spliterator(), false)
                .map(id -> findById(id).orElse(null))
                .toList();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteById(String s) {
        try {
            Files.delete(getImageFile(s));
        } catch (final IOException e) {
            // TODO: better exception handling or should ignore?
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Image entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        strings.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends Image> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        try {
            // first delete all
            Files.delete(basePath);
            // then recreate empty
            createBaseFolder();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
