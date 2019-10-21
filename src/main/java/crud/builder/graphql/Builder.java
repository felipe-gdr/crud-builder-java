package crud.builder.graphql;

import crud.builder.database.Database;
import crud.builder.model.Root;
import crud.builder.parser.Parser;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Builder {
    private final Database database;

    public Builder(Database database) {
        this.database = database;
    }

    /**
     * Build a complete {@link GraphQL} object, containing types, operations and wiring based
     * on a json data model
     *
     * @param jsonModel the model in Json format.
     * @return a GraphQL object ready to be used to perform operations.
     */
    @Nonnull
    public GraphQL buildGraphQL(String jsonModel) {
        Root root = new Parser().toRootModel(jsonModel);
        GraphQLSchema schema = new Types().buildTypeSchema(root);
        RuntimeWiring wiring = new Wiring(database).createWiring(root);

        String stringSchema = new ToString().convertToString(schema);
        // TODO: create a schema cache or something

        SchemaParser schemaParser = new SchemaParser();
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        TypeDefinitionRegistry typeRegistry = schemaParser.parse(stringSchema);
        GraphQLSchema executableSchema = schemaGenerator.makeExecutableSchema(typeRegistry, wiring);

        return GraphQL
                .newGraphQL(executableSchema)
                .build();
    }
}
