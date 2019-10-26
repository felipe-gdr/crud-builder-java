package crud.builder.queries;

import crud.builder.database.Database;
import crud.builder.database.inmemory.InMemoryDatabase;
import crud.builder.graphql.Builder;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QueryTest {
    private static String userModel;

    @BeforeClass
    public static void setup() throws IOException {
        userModel = IOUtils.resourceToString("/user-model.json", UTF_8);
    }

    @Test
    public void add() {
        Database inMemoryDatabase = new InMemoryDatabase();
        GraphQL graphQL = new Builder(inMemoryDatabase).buildGraphQL(userModel);

        String addUserQuery = " mutation { addUser(name: \"Ziggy\" email: \"ziggy@cats.com\") {id name email} } ";

        ExecutionResult result = graphQL.execute(addUserQuery);

        assertThat(result.getErrors().size(), is(0));

        Map<String, Map<String, Object>> data = result.getData();
        Map<String, Object> user = data.get("addUser");

        assertThat(user.get("id"), instanceOf(String.class));
        assertThat(user.get("name"), is("Ziggy"));
        assertThat(user.get("email"), is("ziggy@cats.com"));
    }

    @Test
    public void query() throws IOException {
        String userData = IOUtils.resourceToString("/mock-data/users.json", UTF_8);

        Database inMemoryDatabase = new InMemoryDatabase(userData);
        GraphQL graphQL = new Builder(inMemoryDatabase).buildGraphQL(userModel);

        String userQuery = " query { user(id: \"1\") {id name email} } ";

        ExecutionResult result = graphQL.execute(userQuery);

        assertThat(result.getErrors().size(), is(0));

        Map<String, Map<String, Object>> data = result.getData();
        Map<String, Object> user = data.get("user");

        assertThat(user.get("id"), is("1"));
        assertThat(user.get("name"), is("Ziggy"));
        assertThat(user.get("email"), is("ziggy@cats.com"));
    }

}
