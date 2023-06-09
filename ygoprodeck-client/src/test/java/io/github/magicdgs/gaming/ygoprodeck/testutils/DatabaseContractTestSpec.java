package io.github.magicdgs.gaming.ygoprodeck.testutils;

import io.github.magicdgs.gaming.ygoprodeck.api.DatabaseApi;
import io.github.magicdgs.gaming.ygoprodeck.model.*;
import io.github.magicdgs.gaming.ygoprodeck.client.exception.YgoprodeckResponseErrorException;
import io.github.magicdgs.gaming.ygoprodeck.model.json.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static io.github.magicdgs.gaming.ygoprodeck.api.DatabaseApi.*;

@Slf4j
public abstract class DatabaseContractTestSpec {

	protected abstract DatabaseApi getDatabaseApi();

	@Test
	public final void testGetCheckDBVer() throws Exception {
		// this might fail with the execute as the error is not understood
		var result = getDatabaseApi().getCheckDBVer().execute().body();
		assertAll(
				() -> assertDoesNotThrow(() -> JsonConverter.asJson(result)),
				() -> assertEquals(1, result.size()),
				() -> assertNotNull(result.get(0).getDatabaseVersion()),
				() -> assertNotNull(result.get(0).getLastUpdate())
		);
	}

	@Test
	public final void testGetCardsets() throws Exception {
		var result =  getDatabaseApi().getCardSets().execute().body();
		assertAll(
				() -> assertDoesNotThrow(() -> JsonConverter.asJson(result)),
				() -> assertTrue(result.size() > 20),
				() -> assertNotNull(result.get(0).getSetName()),
				() -> assertNotNull(result.get(0).getSetCode())
		);
	}
	
	@Test
	public final void testGetArchetypes() throws Exception {
		var result = getDatabaseApi().getArchetypes().execute().body();
		assertAll(
				() -> assertDoesNotThrow(() -> JsonConverter.asJson(result)),
				() -> assertTrue(result.size() > 20),
				() -> assertNotNull(result.get(0).getArchetypeName())
		);
	}

	@Test
	public final void testGetRandomCard() throws Exception {
		var result = getDatabaseApi().getRandomCard().execute().body();
		assertAll(
				() -> assertDoesNotThrow(() -> JsonConverter.asJson(result)),
				() -> assertNotNull(result.getName()),
				() -> assertNotNull(result.getId())
		);
	}
	
	@Test
	public final void testGetCardsetInfo() throws Exception {
		String expectedSetCode = YgoprodeckFilesResources.CARDSETINFO_SETCODE_PARAM;
		final var params = new GetCardSetInfoQueryMap()
				.setcode(expectedSetCode);
		var result = getDatabaseApi().getCardSetInfo(params).execute().body();
		assertAll(
				() -> assertDoesNotThrow(() -> JsonConverter.asJson(result)),
				() -> assertEquals(expectedSetCode, result.getSetCode()),
				() -> assertNotNull(result.getId()),
				() -> assertNotNull(result.getName())
		);
	}
	
	@Test
	public final void testGetCardinfoWithoutParams() throws Exception {
		var result = getDatabaseApi().getCardInfo(new GetCardInfoQueryMap())
				.execute().body();
		assertAll(
				() -> assertDoesNotThrow(() -> JsonConverter.asJson(result)),
				() -> assertTrue(result.getData().size() > 1000),
				() -> assertNull(result.getData().get(0).getMiscInfo()),
				() -> assertNull(result.getMeta())
		);
	}
	
	@Test
	public final void testGetCardinfoWithMiscParam() throws Exception {
		final var params = new GetCardInfoQueryMap().misc(YesSwitch.YES);
		var result = getDatabaseApi().getCardInfo(params)
				.execute().body();
		assertAll(
				() -> assertDoesNotThrow(() -> JsonConverter.asJson(result)),
				// a list of 1 element
				() -> assertEquals(1, result.getData().get(0).getMiscInfo().size()),
				() -> assertNotNull(result.getData().get(0).getMiscInfo().get(0).getKonamiId()),
				() -> assertNull(result.getMeta())
		);
	}
	
	@Test
	public final void testGetCardinfoWithPagination() throws Exception {
		final var params = new GetCardInfoQueryMap()
				.num(YgoprodeckFilesResources.CARDINFO_PAGINATED_NUM_PARAM)
				.offset(YgoprodeckFilesResources.CARDINFO_PAGINATED_OFFSET_PARAM);
		var result = getDatabaseApi().getCardInfo(params)
				.execute().body();
		assertAll(
				() -> assertDoesNotThrow(() -> JsonConverter.asJson(result)),
				() -> assertEquals(YgoprodeckFilesResources.CARDINFO_PAGINATED_NUM_PARAM, result.getData().size()),
				() -> assertNull(result.getData().get(0).getMiscInfo()),
				() -> assertNotNull(result.getMeta()),
				() -> assertEquals(YgoprodeckFilesResources.CARDINFO_PAGINATED_NUM_PARAM, result.getMeta().getCurrentRows()),
				() -> assertEquals(YgoprodeckFilesResources.CARDINFO_PAGINATED_NUM_PARAM, result.getMeta().getNextPageOffset())
		);
	}
	
	@Test
	public final void testGetCardInfoWithWrongTypeParameter() throws Exception {
		final var params = new GetCardInfoQueryMap()
				.type(List.of(YgoprodeckFilesResources.CARDINFO_TYPE_ERROR_PARAM));
		final var exception = assertThrows(YgoprodeckResponseErrorException.class, //
				() -> getDatabaseApi().getCardInfo(params).execute().body());
		assertNotNull(exception.getError());
	}

}
