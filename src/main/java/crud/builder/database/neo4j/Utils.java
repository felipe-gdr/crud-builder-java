package crud.builder.database.neo4j;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.text.WordUtils.capitalize;

@ParametersAreNonnullByDefault
public final class Utils {

    @Nonnull
    public static String labelName(String entityName) {
        return capitalize(entityName);
    }

    private static final ObjectMapper toNeo4JNodeValueMapper = new ObjectMapper();

    static {
        toNeo4JNodeValueMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
    }

    @Nonnull
    public static String toNeo4JNodeValue(Map<String, Object> map) {
        try {
            return toNeo4JNodeValueMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error writing map value to string. " + map);
        }
    }

    @Nonnull
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
