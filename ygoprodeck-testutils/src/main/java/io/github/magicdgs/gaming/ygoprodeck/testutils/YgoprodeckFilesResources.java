package io.github.magicdgs.gaming.ygoprodeck.testutils;

import java.net.URI;
import java.nio.file.*;
import java.util.Collections;

public class YgoprodeckFilesResources {

	private static URI YGOPRODECK_FILES_URI = null;

	// DB INFO
	public static final String CHECKDBVER_RESOURCE = "checkDBVer.php.json";

	// CARDINFO
	public static final String CARDINFO_RESOURCE = "cardinfo.php.json";
	public static final String CARDINFO_MISC_RESOURCE = "cardinfo.php_misc.json";
	public static final int CARDINFO_PAGINATED_NUM_PARAM = 5;
	public static final int CARDINFO_PAGINATED_OFFSET_PARAM = 0;
	public static final String CARDINFO_PAGINATED_RESOURCE = "cardinfo.php_num5_offset0.json";
	// errors
	public static final String CARDINFO_TYPE_ERROR_PARAM = "wood";
	public static final String CARDINFO_TYPE_ERROR_RESOURCE = "cardinfo.php_type_wood.json";

	// CARDSETS
	public static final String CARDSETS_RESOURCE = "cardsets.php.json";

	// SPECIFIC CARDS ENDPOINTS
	public static final String CARDSETINFO_SETCODE_PARAM = "SDY-046";
	public static final String CARDSETINFO_SETCODE_RESOURCE = "cardsetinfo_" + CARDSETINFO_SETCODE_PARAM + ".php.json";
	public static final String RANDOMCARD_RESOURCE = "randomcard.php.json";

	// DOMAIN ENDPOINTS
	public static final String ARCHETYPES_RESOURCE = "archetypes.php.json";

	public static Path getResource(final String jsonResource) throws Exception {
		ensureFilesystem();
		return Paths.get(YGOPRODECK_FILES_URI).resolve(jsonResource);
	}

	private synchronized static void ensureFilesystem() throws Exception {
		if (YGOPRODECK_FILES_URI == null) {
			YGOPRODECK_FILES_URI = YgoprodeckFilesResources.class.getClassLoader()
					.getResource("ygoprodeck-files").toURI();
			try {
				Paths.get(YGOPRODECK_FILES_URI);
			} catch (FileSystemNotFoundException e) {
				// if this happens, we are getting it from the zipfs
				FileSystem zipfs = FileSystems.newFileSystem(
						YGOPRODECK_FILES_URI,
						Collections.singletonMap("create", "true"));
			}
		}
	}


}
