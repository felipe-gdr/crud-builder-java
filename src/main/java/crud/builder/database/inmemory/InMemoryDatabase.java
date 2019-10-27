package crud.builder.database.inmemory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import crud.builder.database.Database;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.toList;

@ParametersAreNonnullByDefault
public class InMemoryDatabase implements Database {
    private final Map<String, Map<String, Map<String, Object>>> data;

    public InMemoryDatabase(@Nullable String jsonData) {
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
        // TODO: handle entity reference (check if "userId" value references an existing user)
        return value -> {
            String id = getNextId(entityName);

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

    @Nonnull
    @Override
    public Function<String, Collection<Map<String, Object>>> buildFindAssociatedCollection(
            String fromEntityName,
            String toEntityName
    ) {
        return id -> data.get(toEntityName)
                .values()
                .stream()
                .filter(value -> value.get(fromEntityName + "Id").equals(id))
                .collect(toList());
    }

    private String getNextId(String entityName) {
        return Optional.ofNullable(data.get(entityName))
                .flatMap(data ->
                        data
                                .values()
                                .stream()
                                .map(value -> value.get("id"))
                                .map(Object::toString)
                                .map(Integer::valueOf)
                                .max(Comparator.naturalOrder())
                )
                .orElse(0) + 1 + "";
    }
}
