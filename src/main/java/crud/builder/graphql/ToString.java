package crud.builder.graphql;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ToString {
    private static final SchemaPrinter schemaPrinter = new SchemaPrinter();

    /**
     * Convert an in-memory {@link GraphQLSchema} to String format
     *
     * @param graphQLSchema the schema
     * @return a string representation of the schema
     */
    public String convertToString(GraphQLSchema graphQLSchema) {
        return schemaPrinter.print(graphQLSchema);
    }
}
