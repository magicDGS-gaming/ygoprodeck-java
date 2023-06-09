package io.github.magicdgs.gaming.ygoprodeck.client.internal;

import io.github.magicdgs.gaming.ygoprodeck.Constants;
import io.github.magicdgs.gaming.ygoprodeck.client.exception.YgoprodeckException;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;

@NoArgsConstructor
@ToString
public abstract class ClientBuilder<T> {

    protected String databaseUrl;
    protected String imagesUrl;
    protected int maxRequest;
    protected Duration duration;
    protected boolean strict = false;

    /**
     * Set the default URLs.
     *
     * @return builder with default URLs.
     */
    public ClientBuilder<T> defaultUrls() {
        return this.dbUrl(Constants.DB_URL) //
                .imagesUrl(Constants.IMAGE_URL);
    }

    public ClientBuilder<T> dbUrl(final String dbUrl) {
        this.databaseUrl = dbUrl;
        return this;
    }

    public ClientBuilder<T> imagesUrl(final String imagesUrl) {
        this.imagesUrl = imagesUrl;
        return this;
    }

    public ClientBuilder<T> commonUrl(final String url) {
        this.databaseUrl = url;
        this.imagesUrl = url;
        return this;
    }

    public ClientBuilder<T> rateLimit(final int maxRequest, Duration duration) {
        this.maxRequest = maxRequest;
        this.duration = duration;
        return this;
    }

    public ClientBuilder<T> strict(final boolean strict) {
        this.strict = strict;
        return this;
    }

    public final T build() throws YgoprodeckException {
        if (databaseUrl == null) {
            throw new YgoprodeckException("Database URL cannot be null");
        }
        if (imagesUrl == null) {
            throw new YgoprodeckException("Images URL cannot be null");
        }
        return buildInstance();
    }

    protected abstract T buildInstance() throws YgoprodeckException;
}
