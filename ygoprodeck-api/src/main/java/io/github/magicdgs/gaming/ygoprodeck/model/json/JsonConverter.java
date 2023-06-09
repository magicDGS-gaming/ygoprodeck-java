package io.github.magicdgs.gaming.ygoprodeck.model.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class with utility methods for JSON conversion.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonConverter {

	private static final ObjectMapper DEFAULT = createObjectMapper(true);
	
	private static SimpleModule YGOPRODECK_MODULE = null;
	
	private static SimpleModule getYgoprodeckModule() {
		if (YGOPRODECK_MODULE == null) {
			final DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			YGOPRODECK_MODULE = new SimpleModule(JsonConverter.class.getPackageName()) //
					.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter)) //
					.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
		}
		return YGOPRODECK_MODULE;
	}

	/**
	 * Creates and {@link ObjectMapper} safe to use with the API.
	 * </br>
	 * The mapper contains some customizations for serialization/deserialization
	 * as the one get from the API.
	 *
	 * @param strict {@code true} if the mapper should fail on unknown properties;
	 *               {@code false} otherwise.
	 *
	 * @return new instance.
	 */
	public static ObjectMapper createObjectMapper(final boolean strict) {
		final JsonMapper.Builder mapperBuilder = JsonMapper.builder() //
				.addModule(new JavaTimeModule())
				.addModule(getYgoprodeckModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, strict);
		return mapperBuilder.build() //
				.setDateFormat(new SimpleDateFormat())
				.setDefaultPropertyInclusion(Include.NON_EMPTY);
	}

	
	/**
	 * Converts the object into a JSON string (pretty-print) with default settings.
	 * </br>
	 * Default settings are the same as an strict {@link #createObjectMapper(boolean)}.
	 * 
	 * @param object the object to serialize.
	 * 
	 * @return json string.
	 */
	public static String asJson(final Object object) throws Exception {
		return DEFAULT.writerWithDefaultPrettyPrinter().writeValueAsString(object);
	}
	
	/**
	 * Converts the object into a JSON object with default settings.
	 * 
	 * @param json string to deserialize.
	 * @param type object type.
	 * 
	 * @return deserialized object.
	 */
	public static <T> T toObject(final String json, final Class<T> type) throws Exception {
		return DEFAULT.readValue(json, type);
	}
	
	
}
