package crud.builder.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import crud.builder.model.Root;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Parser {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Convert the model from Json to in memory objects
     * @param jsonModel the model in Json format
     * @return a {@link Root} object representing the top level model
     */
    @Nonnull
    public Root toRootModel(String jsonModel) {
        try {
            return mapper.readValue(jsonModel, Root.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing json model", e);
        }
    }

}
