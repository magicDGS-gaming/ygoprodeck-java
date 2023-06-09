package io.github.magicdgs.gaming.ygoprodeck.model.json;

import io.github.magicdgs.gaming.ygoprodeck.model.Attribute;
import io.github.magicdgs.gaming.ygoprodeck.model.Card;
import io.github.magicdgs.gaming.ygoprodeck.model.CardSetItemDTO;
import io.github.magicdgs.gaming.ygoprodeck.model.CheckDBVersionDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class JsonConverterTest {

	@Test
	public void testModelWithDateTime() throws Exception {
		final String asString = "{\"database_version\":\"90.83\",\"last_update\":\"2023-03-05 21:28:40\"}";
		var deserializedObject = Assertions.assertDoesNotThrow(() -> JsonConverter.toObject(asString, CheckDBVersionDTO.class));
		String serializedString = JsonConverter.createObjectMapper(true).writeValueAsString(deserializedObject);
		Assertions.assertEquals(asString, serializedString);
	}
	
	@Test
	public void testModelWitDate() throws Exception {
		final String asString = "{\"set_name\":\"2-Player Starter Deck: Yuya & Declan\",\"set_code\":\"YS15\",\"num_of_cards\":42,\"tcg_date\":\"2015-05-28\"}";
		var deserializedObject = Assertions.assertDoesNotThrow(() -> JsonConverter.toObject(asString, CardSetItemDTO.class));
		String serializedString = JsonConverter.createObjectMapper(true).writeValueAsString(deserializedObject);
		Assertions.assertEquals(asString, serializedString);
	}
	
	@ValueSource(strings = {
			"DARK",
			"dark",
			"Dark"
	})
	@ParameterizedTest
	public void testCaseInsensitiveEnumDeserializer(final String darkAttributeVariant) throws Exception {
		final String asString = "{\"attribute\":\"dark\"}";
		var deserializedObject = Assertions.assertDoesNotThrow(() -> JsonConverter.toObject(asString, Card.class));
		Assertions.assertEquals(Attribute.DARK, deserializedObject.getAttribute());
	}
}
