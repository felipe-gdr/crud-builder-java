package crud.builder.database.neo4j;

import com.google.common.collect.ImmutableMap;
import crud.builder.database.Database;
import crud.builder.database.neo4j.querybuilder.Create;
import crud.builder.database.neo4j.querybuilder.FindById;
import org.neo4j.driver.v1.*;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static crud.builder.database.neo4j.Utils.generateId;
import static crud.builder.database.neo4j.Utils.labelName;

@ParametersAreNonnullByDefault
public class Neo4JDatabase implements Database, AutoCloseable {
    private final Driver driver;

    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "1234";

    public Neo4JDatabase() {
        this.driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));
    }

    @Nonnull
    @Override
    public UnaryOperator<Map<String, Object>> buildAddEntity(String entityName) {
        return value -> {
            String id = generateId();

            ImmutableMap<String, Object> mapWithId = ImmutableMap.<String, Object>builder()
                    .putAll(value)
                    .put("id", id)
                    .build();

            String query = new Create()
                    .label(labelName(entityName))
                    .value(mapWithId)
                    .get();

            try (Session session = driver.session()) {
                return session.writeTransaction(
                        transaction -> {
                            StatementResult result = transaction.run(query);

                            return result.single().get(0).asMap();
                        }
                );
            }
        };
    }

    @Nonnull
    @Override
    public Function<String, Map<String, Object>> buildFindEntityById(String entityName) {
        return id -> {
            String query = new FindById()
                    .label(labelName(entityName))
                    .id(id)
                    .get();

            try (Session session = driver.session()) {
                return session.readTransaction(
                        transaction -> {
                            StatementResult result = transaction.run(query);

                            return result.single().get(0).asMap();
                        }
                );
            }
        };
    }

    @Nonnull
    @Override
    public Function<String, Collection> buildFindOneToManyCollection(String fromEntityName, String toEntityName) {
        // TODO: implement me
        return null;
    }

    @Override
    public void close() {
        this.driver.close();
    }

    @Nonnull
    @Override
    public <T> Function<String, Collection<T>> buildFindManyToManyCollection(String fromEntityName, String toEntityName) {
        // TODO: implement me
        return null;
    }
}
