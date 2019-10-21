package crud.builder.database.neo4j;

import com.google.common.collect.ImmutableMap;
import crud.builder.database.Database;
import crud.builder.database.neo4j.querybuilder.Create;
import crud.builder.database.neo4j.querybuilder.FindById;
import crud.builder.model.Root.Entity;
import org.neo4j.driver.v1.*;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Consumer;
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
    public UnaryOperator<Map<String, Object>> buildCreate(Entity entity) {
        return value -> {
            String id = generateId();

            ImmutableMap<String, Object> mapWithId = ImmutableMap.<String, Object>builder()
                    .putAll(value)
                    .put("id", id)
                    .build();

            String query = new Create()
                    .label(labelName(entity.getName()))
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
    public Function<String, Map<String, Object>> buildRead(Entity entity) {
        return id -> {
            String query = new FindById()
                    .label(labelName(entity.getName()))
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
    public <T> UnaryOperator<T> buildUpdate(Entity entity) {
        return null;
    }

    @Nonnull
    @Override
    public <T> Consumer<T> buildDelete(Entity entity) {
        return null;
    }

    @Override
    public void close() throws Exception {
        this.driver.close();
    }
}
