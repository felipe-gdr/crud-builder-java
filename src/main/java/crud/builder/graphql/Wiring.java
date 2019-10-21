package crud.builder.graphql;

import crud.builder.database.Database;
import crud.builder.model.Root;
import crud.builder.model.Root.Entity;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static crud.builder.graphql.Utils.createMutationName;

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
        return RuntimeWiring.newRuntimeWiring()
                .type("QueryType", queryWiring(root.getEntities()))
                .type("MutationType", mutationWiring(root.getEntities()))
                .build();
    }

    @Nonnull
    private UnaryOperator<TypeRuntimeWiring.Builder> queryWiring(List<Entity> entities) {
        return typeWiring -> {

            TypeRuntimeWiring.Builder partialWiring = typeWiring;

            for (Entity entity : entities) {
                Function<String, Object> findById = database.buildRead(entity);

                partialWiring = partialWiring.dataFetcher(
                        entity.getName(),
                        env -> findById.apply(env.getArgument("id"))
                );
            }

            return typeWiring;
        };
    }
    @Nonnull
    private UnaryOperator<TypeRuntimeWiring.Builder> mutationWiring(List<Entity> entities) {
        return typeWiring -> {

            TypeRuntimeWiring.Builder partialWiring = typeWiring;

            for (Entity entity : entities) {
                UnaryOperator<Object> create = database.buildCreate(entity);

                partialWiring = partialWiring.dataFetcher(
                        createMutationName(entity.getName()),
                        env -> create.apply(env.getArguments())
                );
            }

            return typeWiring;
        };
    }
}
