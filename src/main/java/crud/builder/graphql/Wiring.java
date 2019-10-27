package crud.builder.graphql;

import crud.builder.database.Database;
import crud.builder.model.Root;
import crud.builder.model.Root.Entity;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static crud.builder.graphql.Utils.createMutationName;
import static crud.builder.graphql.Utils.toTypeName;
import static crud.builder.model.Root.Field.FieldType.*;
import static java.util.stream.Collectors.toList;

@ParametersAreNonnullByDefault
public class Wiring {
    private final Database database;

    public Wiring(Database database) {
        this.database = database;
    }

    /**
     * Creates a {@link RuntimeWiring} defining the links between GraphQL operations defined
     * in the schema (queries, mutations, subscriptions) based on the {@link Root} model.
     *
     * @param root the model.
     * @return a {@link RuntimeWiring} defining GraphQL operations.
     */
    @Nonnull
    public RuntimeWiring createWiring(Root root) {
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring()
                .type("QueryType", queryWiring(root.getEntities()))
                .type("MutationType", mutationWiring(root.getEntities()));

        builder = relationshipWiring(builder, root.getEntities());

        return builder.build();
    }

    @Nonnull
    private UnaryOperator<TypeRuntimeWiring.Builder> queryWiring(List<Entity> entities) {
        return typeWiring -> {

            TypeRuntimeWiring.Builder partialWiring = typeWiring;

            for (Entity entity : entities) {
                Function<String, Object> findById = database.buildFindEntityById(entity.getName());

                partialWiring = partialWiring.dataFetcher(
                        entity.getName(),
                        env -> findById.apply(env.getArgument("id"))
                );

            }

            return typeWiring;
        };
    }

    @Nonnull
    private RuntimeWiring.Builder relationshipWiring(RuntimeWiring.Builder builder, List<Entity> entities) {
        for (Entity entity : entities) {
            List<Root.Field> relationships = this.getRelationships(entity);

            if (relationships.isEmpty()) {
                continue;
            }

            builder = builder.type(
                    toTypeName(entity.getName()),
                    typeWiring -> {
                        for (Root.Field relationship : relationships) {
                            if (relationship.getType() == ONE_TO_ONE || relationship.getType() == MANY_TO_ONE) {
                                Function<String, Object> findById = database.buildFindEntityById(relationship.getEntity());

                                typeWiring = typeWiring.dataFetcher(
                                        relationship.getName(),
                                        env -> {
                                            Map<String, Object> source = env.getSource();

                                            Object idValue = source.get(Utils.getIdFieldName(relationship.getEntity()));

                                            return findById.apply(idValue.toString());
                                        }
                                );
                            } else if (relationship.getType() == ONE_TO_MANY || relationship.getType() == MANY_TO_MANY) {
                                Function<String, Collection<Object>> findAssociatedCollection =
                                        database.buildFindAssociatedCollection(entity.getName(), relationship.getEntity());

                                typeWiring = typeWiring.dataFetcher(
                                        relationship.getName(),
                                        env -> {
                                            Map<String, Object> source = env.getSource();

                                            Object idValue = source.get("id");

                                            return findAssociatedCollection.apply(idValue.toString());
                                        }
                                );
                            }
                        }

                        return typeWiring;
                    }
            );

        }

        return builder;
    }

    private List<Root.Field> getRelationships(Entity entity) {
        return entity.getFields().stream()
                .filter(field -> Root.Field.FieldType.isRelationship(field.getType()))
                .collect(toList());
    }

    @Nonnull
    private UnaryOperator<TypeRuntimeWiring.Builder> mutationWiring(List<Entity> entities) {
        return typeWiring -> {
            for (Entity entity : entities) {
                UnaryOperator create = database.buildAddEntity(entity.getName());

                typeWiring = typeWiring.dataFetcher(
                        createMutationName(entity.getName()),
                        env -> create.apply(env.getArguments())
                );
            }

            return typeWiring;
        };
    }
}
