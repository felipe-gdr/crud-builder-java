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
    <T> Function<String, Collection<T>> buildFindAssociatedCollection(String fromEntityName, String toEntityName);
}
