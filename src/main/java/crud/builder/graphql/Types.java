package crud.builder.graphql;

import crud.builder.model.Root;
import crud.builder.model.Root.Entity;
import crud.builder.model.Root.Field;
import graphql.schema.*;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.List;

import static crud.builder.graphql.Utils.createMutationName;
import static crud.builder.graphql.Utils.toTypeName;
import static graphql.Scalars.*;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLList.list;
import static graphql.schema.GraphQLNonNull.nonNull;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;
import static graphql.schema.GraphQLTypeReference.typeRef;
import static java.util.stream.Collectors.toList;

@ParametersAreNonnullByDefault
public class Types {
    /**
     * Create a {@link GraphQLSchema} defining the types, queries, mutations and
     * subscriptions based on a {@link Root} model.
     *
     * @param root the root model.
     * @return a GraphQLSchema defining the types.
     */
    @Nonnull
    public GraphQLSchema buildTypeSchema(Root root) {
        GraphQLObjectType queryType = buildQueryType(root);
        GraphQLObjectType mutationType = buildMutationType(root);

        return newSchema()
                .query(queryType)
                .mutation(mutationType)
                .build();
    }

    @Nonnull
    private GraphQLObjectType buildQueryType(Root root) {
        GraphQLObjectType.Builder builder = newObject().name("QueryType");

        // Can we be more functional here?
        for (Entity entity : root.getEntities()) {
            GraphQLObjectType objectType = this.buildObjectType(entity);

            // Build id argument for querying entities by id.
            GraphQLArgument idArgument = newArgument()
                    .name("id")
                    .type(nonNull(GraphQLString))
                    .build();

            builder = builder
                    .field(newFieldDefinition()
                            .name(entity.getName())
                            .argument(idArgument)
                            .type(objectType));
        }

        return builder.build();
    }

    @Nonnull
    private GraphQLObjectType buildMutationType(Root root) {

        GraphQLObjectType.Builder builder = newObject().name("MutationType");

        // Can we be more functional here?
        for (Entity entity : root.getEntities()) {
            List<GraphQLArgument> arguments = entity.getFields().stream()
                    // TODO: deal with relationships
                    // the instanceof check is here to add only scalars to arguments of add mutations
                    .filter(field -> getGraphQLInputType(field) instanceof GraphQLScalarType)
                    .map(field ->
                            newArgument()
                                    .name(field.getName())
                                    .type(
                                            field.getRequired()
                                                    ? nonNull(getGraphQLInputType(field))
                                                    : getGraphQLInputType(field)
                                    )
                                    .build()
                    )
                    .collect(toList());

            builder = builder
                    .field(newFieldDefinition()
                            .name(createMutationName(entity.getName()))
                            .arguments(arguments)
                            .type(GraphQLTypeReference.typeRef(toTypeName(entity.getName())))
                    );
        }

        return builder.build();
    }

    // Maybe use a map instead of switch case?
    @Nonnull
    private GraphQLType getGraphQLType(Field field) {
        switch (field.getType()) {
            case MANY_TO_ONE:
                return typeRef(toTypeName(field.getEntity()));
            case ONE_TO_MANY:
                return list(typeRef(toTypeName(field.getEntity())));
            case STRING:
                return GraphQLString;
            case INTEGER:
                return GraphQLInt;
            default:
                throw new RuntimeException("Type not implemented " + field.getType());
        }
    }

    @Nonnull
    private GraphQLInputType getGraphQLInputType(Field field) {
        return (GraphQLInputType) this.getGraphQLType(field);
    }

    @Nonnull
    private GraphQLOutputType getGraphQLOutputType(Field field) {
        return (GraphQLOutputType) this.getGraphQLType(field);
    }

    @Nonnull
    private GraphQLObjectType buildObjectType(Entity entity) {
        String entityName = entity.getName();
        String typeName = toTypeName(entityName);

        GraphQLObjectType.Builder builder = newObject()
                // Add id field to all entities
                .field(newFieldDefinition().name("id").type(nonNull(GraphQLID)))
                .name(typeName);

        // Can we do this on a more functional way, without mutating "builder" on a loop?
        for (Field field : entity.getFields()) {
            GraphQLOutputType graphQLType = getGraphQLOutputType(field);

            if (field.getRequired()) {
                graphQLType = nonNull(graphQLType);
            }

            builder = builder.field(newFieldDefinition().name(field.getName()).type(graphQLType));
        }

        return builder.build();
    }
}
