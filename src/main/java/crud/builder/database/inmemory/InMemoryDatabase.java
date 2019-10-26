package crud.builder.database.inmemory;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import crud.builder.database.Database;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class InMemoryDatabase implements Database {
    private final Map<String, Map<String, Map<String, Object>>> data;

    public InMemoryDatabase(String jsonData) {
        this.data = new HashMap<>();

        if (jsonData != null) {
            try {
                Map map = new ObjectMapper().readValue(jsonData, Map.class);
                data.putAll(map);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error parsing json data: " + jsonData, e);
            }
        }
    }

    public InMemoryDatabase() {
        this(null);
    }

    @Nonnull
    @Override
    public UnaryOperator<Map<String, Object>> buildAddEntity(String entityName) {

        return value -> {
            String id = UUID.randomUUID().toString();
            ImmutableMap<String, Object> valueWithId = ImmutableMap
                    .<String, Object>builder()
                    .putAll(value)
                    .put("id", id)
                    .build();

            data.getOrDefault(entityName, new HashMap<>()).put(id, value);

            return valueWithId;
        };
    }

    @Nonnull
    @Override
    public Function<String, Map<String, Object>> buildFindEntityById(String entityName) {
        return id -> data.get(entityName).get(id);
    }
}
