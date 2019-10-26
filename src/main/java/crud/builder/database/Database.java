package crud.builder.database;

import crud.builder.model.Root;
import crud.builder.model.Root.Field;

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
}
