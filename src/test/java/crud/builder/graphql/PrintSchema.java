package crud.builder.graphql;

import crud.builder.database.Database;
import crud.builder.database.inmemory.InMemoryDatabase;
import graphql.GraphQL;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.resourceToString;

public class PrintSchema {
    private static final String MODEL_PATH = "/user-model.json";

    public void test() throws IOException {
        String userModel = resourceToString(MODEL_PATH, UTF_8);
        Database inMemoryDatabase = new InMemoryDatabase(null);

        GraphQL graphQL = new Builder(inMemoryDatabase).buildGraphQL(userModel);
    }
}
