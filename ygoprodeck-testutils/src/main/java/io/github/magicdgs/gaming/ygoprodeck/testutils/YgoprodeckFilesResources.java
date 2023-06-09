package io.github.magicdgs.gaming.ygoprodeck.testutils;

import java.net.URI;
import java.nio.file.*;
import java.util.Collections;

import static io.github.magicdgs.gaming.ygoprodeck.api.DatabaseApi.*;

/**
 * Test resources to return from mock servers or load.
 * </br>
 * Those resources are bundled on the test-utils and can be loaded with {@link #getResource(String)},
 * where the resource name are the constants on this class.
 * </br>
 * To use directly with a mock-server, please use {@link YgoprodeckMockServerFactory#createMockWebServer()}.
 */
public class YgoprodeckFilesResources {

	private static URI YGOPRODECK_FILES_URI = null;


	// DB INFO
	public static final String CHECKDBVER_RESOURCE = GET_CHECK_DB_VER_PATH + ".json";

	// CARDINFO
	public static final String CARDINFO_RESOURCE = GET_CARD_INFO_PATH + ".json";
	public static final String CARDINFO_MISC_RESOURCE = GET_CARD_INFO_PATH + "_misc.json";
	public static final int CARDINFO_PAGINATED_NUM_PARAM = 5;
	public static final int CARDINFO_PAGINATED_OFFSET_PARAM = 0;
	public static final String CARDINFO_PAGINATED_RESOURCE = GET_CARD_INFO_PATH + "_num5_offset0.json";
	// errors
	public static final String CARDINFO_TYPE_ERROR_PARAM = "wood";

	public static final String CARDINFO_TYPE_ERROR_RESOURCE = GET_CARD_INFO_PATH + "_type_wood.json";

	// CARDSETS
	public static final String CARDSETS_RESOURCE = GET_CARD_SETS_PATH + ".json";

	// SPECIFIC CARDS ENDPOINTS
	public static final String CARDSETINFO_SETCODE_PARAM = "SDY-046";
	public static final String CARDSETINFO_SETCODE_RESOURCE = GET_CARD_SET_INFO_PATH + "_" + CARDSETINFO_SETCODE_PARAM + ".json";

	public static final String RANDOMCARD_RESOURCE = GET_RANDOM_CARD_PATH + ".json";

	// DOMAIN ENDPOINTS
	public static final String ARCHETYPES_RESOURCE = "archetypes.php.json";

	/**
	 * Gets the resource path to load the information.
	 *
	 * @param resourceName name of the resource.
	 *
	 * @return path to the resource.
	 */
	public static Path getResource(final String resourceName) {
		ensureFilesystem();
		return Paths.get(YGOPRODECK_FILES_URI).resolve(resourceName);
	}

	private synchronized static void ensureFilesystem() {
		try {
			if (YGOPRODECK_FILES_URI == null) {
				YGOPRODECK_FILES_URI = YgoprodeckFilesResources.class.getClassLoader()
						.getResource("ygoprodeck-files").toURI();
				try {
					Paths.get(YGOPRODECK_FILES_URI);
				} catch (FileSystemNotFoundException e) {
					// if this happens, we are getting it from the zipfs
					// IMPORTANT: we should call this method and
					// do not close the filesystem to allow the files to be accessed
					FileSystem zipfs = FileSystems.newFileSystem(
							YGOPRODECK_FILES_URI,
							Collections.singletonMap("create", "true"));
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException("Cannot get filesystem");
		}
	}

}
