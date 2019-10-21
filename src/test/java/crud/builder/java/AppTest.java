package crud.builder.java;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import crud.builder.database.Database;
import crud.builder.database.neo4j.Neo4JDatabase;
import crud.builder.graphql.Builder;
import crud.builder.model.Root.Entity;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static graphql.Scalars.GraphQLID;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLList.list;
import static graphql.schema.GraphQLNonNull.nonNull;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;
import static graphql.schema.GraphQLTypeReference.typeRef;
import static java.nio.charset.StandardCharsets.UTF_8;

public class AppTest {
    //    final String query = " query { root { parent { child { goodField badField } } } }";
    final String userQuery = " query { user(id: \"94390e70-efbf-11e9-8e8a-bb303c27f40c\") { id name email } }";
    final String todoQuery = " query { todo { id title description user { id name email } } }";
    final String todoQueryNoUser = " query { todo { id title description } }";
    final String queryAll = " query { user { id name email todos { title } } todo { id title description user { name } } }";

    final String addUser = " mutation { addUser(name: \"Ziggy\" email: \"ziggy@cats.com\") {id name email} } ";

    @Test
    public void shouldAnswerWithTrue() {
        GraphQLObjectType userType = newObject()
                .name("User")
                .field(newFieldDefinition().name("id").type(nonNull(GraphQLID)))
                .field(newFieldDefinition().name("name").type(GraphQLString))
                .field(newFieldDefinition().name("email").type(GraphQLString))
                .field(newFieldDefinition().name("todos").type(list(typeRef("Todo"))))
                .build();

        GraphQLObjectType todoType = newObject()
                .name("Todo")
                .field(newFieldDefinition().name("id").type(nonNull(GraphQLID)))
                .field(newFieldDefinition().name("title").type(GraphQLString))
                .field(newFieldDefinition().name("description").type(GraphQLString))
                .field(newFieldDefinition().name("user").type(typeRef("User")))
                .build();

//        GraphQLObjectType newTodoType = newObject(todoType)
//                .field(newFieldDefinition().name("user").type(userType).build())
//                .build();

//        GraphQLObjectType newUserType = newObject(userType)
//                .field(newFieldDefinition().name("todos").type(list(todoType)).build())
//                .build();

        GraphQLSchema schema = newSchema().query(
                newObject()
                        .name("QueryType")
                        .field(newFieldDefinition()
                                .name("user")
                                .type(userType)
                        )
                        .field(newFieldDefinition()
                                .name("todo")
                                .type(todoType)
                        )
        )
                .build();


        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("QueryType", typeWiring -> typeWiring
                        .dataFetcher("user", env -> buildUser())
                        .dataFetcher("todo", env -> buildTodo())
                )
                .type("User", typeWiring -> typeWiring
                        .dataFetcher("todos", env -> buildTodos())
                )
                .type("Todo", typeWiring -> typeWiring
                        .dataFetcher("user", env -> buildUser())
                )
                .build();


        String print = new SchemaPrinter().print(schema);

        // print format of schema will be stored somewhere
//        System.out.println(print);

        // Create executable GraphQL schema from "print" schema
        SchemaParser schemaParser = new SchemaParser();
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        TypeDefinitionRegistry typeRegistry = schemaParser.parse(print);
        GraphQLSchema executableSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);

        GraphQL graphql = GraphQL
                .newGraphQL(executableSchema)
                .build();

        ExecutionResult result = graphql.execute(queryAll);
        Object data = result.getData();
        List<GraphQLError> errors = result.getErrors();

        System.out.println(data);
        System.out.println(errors);
    }

    private Collection<Map<Object, Object>> buildTodos() {
        return Collections.singleton(buildTodo());
    }

    private Map<Object, Object> buildTodo() {
        return ImmutableMap
                .builder()
                .put("id", 1)
                .put("title", "Do")
                .put("description", "this")
                .build();

    }

    private Map<Object, Object> buildUser() {
        ImmutableMap.Builder<Object, Object> builder = ImmutableMap
                .builder()
                .put("id", 1)
                .put("name", "Felipe")
                .put("email", "felipe.gdr@gmail.com");

        return builder.build();
    }

    @Test
    public void testParseJson() throws IOException {
        String jsonModel = IOUtils.resourceToString("/user-model.json", UTF_8);

        Database database = new Neo4JDatabase();

        GraphQL graphQL = new Builder(database).buildGraphQL(jsonModel);

        ExecutionResult result = graphQL.execute(userQuery);
        Object data = result.getData();
        List<GraphQLError> errors = result.getErrors();

        System.out.println(data);
        System.out.println(errors);
    }

    @Test
    public void testNeo4J() throws JsonProcessingException {
        Entity entity = new Entity();

        entity.setName("user");
//
//        Function<String, Map<String, Object>> findById = new Neo4JDatabase().buildRead(entity);
//
//        Map<String, Object> result = findById.apply("94390e70-efbf-11e9-8e8a-bb303c27f40c");

        Map<String, Object> user = ImmutableMap.<String, Object>builder()
                .put("name", "Felipe")
                .put("age", 123)
                .put("male", true)
                .put("height", 1.73)
                .build();

        UnaryOperator<Map<String, Object>> create = new Neo4JDatabase().buildCreate(entity);

        Map<String, Object> result = create.apply(user);

        System.out.println(result);
    }

    @Test
    public void mutation() throws IOException {
        String jsonModel = IOUtils.resourceToString("/user-model.json", UTF_8);

        Database database = new Neo4JDatabase();

        GraphQL graphQL = new Builder(database).buildGraphQL(jsonModel);

        ExecutionResult result = graphQL.execute(addUser);

        Object data = result.getData();
        List<GraphQLError> errors = result.getErrors();

        System.out.println(data);
        System.out.println(errors);
    }
}
