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
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
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
    public Function<String, Collection<Map<String, Object>>> buildFindOneToManyCollection(
            String fromEntityName,
            String toEntityName
    ) {
        return id -> data.get(toEntityName)
                .values()
                .stream()
                .filter(value -> value.get(fromEntityName + "Id").equals(id))
                .collect(toList());
    }

    @Nonnull
    @Override
    public Function<String, Collection<Map<String, Object>>> buildFindManyToManyCollection(String fromEntityName, String toEntityName) {
        String tableName = Stream.of(fromEntityName, toEntityName)
                .sorted()
                .collect(joining("_"));

        boolean isFromLeftEntity = tableName.startsWith(fromEntityName);

        return id -> {
            List ids = isFromLeftEntity
                    ? (List) data.get(tableName).get(id)
                    : data.get(tableName)
                    .entrySet()
                    .stream()
                    .filter(entry ->
                            ((List) entry.getValue())
                                    .stream()
                                    .filter(value -> value.equals(id))
                                    .findAny()
                                    .isPresent()
                    )
                    .map(Map.Entry::getKey)
                    .collect(toList());

            return data.get(toEntityName)
                    .entrySet()
                    .stream()
                    .filter(entry -> ids.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .collect(toList());
        };
    }


    @Nonnull
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
