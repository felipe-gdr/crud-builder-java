package crud.builder.database;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@ParametersAreNonnullByDefault
public interface Database {
    @Nonnull
    <T> UnaryOperator<T> buildAddEntity(String entityName);

    @Nonnull
    <T> Function<String, T> buildFindEntityById(String entityName);

    @Nonnull
    <T> Function<String, Collection<T>> buildFindOneToManyCollection(String fromEntityName, String toEntityName);

    @Nonnull
    <T> Function<String, Collection<T>> buildFindManyToManyCollection(String fromEntityName, String toEntityName);
}
