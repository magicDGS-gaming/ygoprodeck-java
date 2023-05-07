package io.github.magicdgs.gaming.ygoprodeck;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Constants {

    /**
     * Default DB URL on the API.
     */
    public static final String DB_URL = "https://db.ygoprodeck.com/api/v7/";

    /**
     * Default Image URL on the API.
     */
    public static final String IMAGE_URL = "https://images.ygoprodeck.com/";

    /**
     * Maximum request as defined from the API.
     * </br>
     * It is not recommended to use the maximum as it might hit the limit or this can change.
     * Please, consider using {@link #DEFAULT_REQUEST_PER_SECOND} instead unless you know what you are doing.
     */
    public static final int MAX_REQUEST_PER_SECOND = 20;

    /**
     * Default request per second on the library client.
     * </br>
     * This should be enough for most use-cases.
     */
    public static final int DEFAULT_REQUEST_PER_SECOND = 5;
}
