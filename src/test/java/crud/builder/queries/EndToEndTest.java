package crud.builder.queries;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import crud.builder.database.Database;
import crud.builder.database.inmemory.InMemoryDatabase;
import crud.builder.graphql.Builder;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.IOUtils.resourceToString;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class EndToEndTest {
    private static final String MODEL_PATH = "/user-model.json";
    private static final String MOCK_DATA_PATH = "/mock-data";
    private static final String TEST_CASES_PATH = "/test-cases/test-cases.json";

    private static String userModel;
    private static Map<String, String> mockData;

    @BeforeAll
    static void loadResources() throws IOException, URISyntaxException {
        mockData = new HashMap<>();
        userModel = resourceToString(MODEL_PATH, UTF_8);

        Files.walk(Paths.get(EndToEndTest.class.getResource(MOCK_DATA_PATH).toURI()))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        mockData.put(
                                file.getFileName().toString(),
                                resourceToString(MOCK_DATA_PATH + "/" + file.getFileName(), UTF_8)
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    static Stream<TestCase> getTestCases() throws IOException {
        List<TestCase> testCases = new ObjectMapper().readValue(
                resourceToString(TEST_CASES_PATH, UTF_8),
                new TypeReference<List<TestCase>>() {
                }
        );

        return testCases.stream();
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    void test(TestCase testCase) {
        final String data = testCase.data;
        final String query = testCase.query;
        final Map expected = testCase.expected;
        final List<String> errors = testCase.errors;

        Database inMemoryDatabase = new InMemoryDatabase(data != null ? mockData.get(data) : null);

        GraphQL graphQL = new Builder(inMemoryDatabase).buildGraphQL(userModel);

        ExecutionResult result = graphQL.execute(query);

        if (errors != null && !errors.isEmpty()) {
            List<String> resultErrors = result.getErrors().stream().map(GraphQLError::getMessage).collect(toList());

            assertThat(resultErrors, is(errors));
        } else {
            assertThat(result.getErrors().size(), is(0));
        }

        Map resultData = result.getData();

        if (expected != null) {
            assertThat(resultData.toString(), is(expected.toString()));
        } else {
            assertThat(resultData, nullValue());
        }
    }
}
