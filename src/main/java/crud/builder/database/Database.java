package crud.builder.database;

import crud.builder.model.Root.Entity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@ParametersAreNonnullByDefault
public interface Database {
    @Nonnull
    <T> UnaryOperator<T> buildCreate(Entity entity);

    @Nonnull
    <T> Function<String, T> buildRead(Entity entity);

    @Nonnull
    <T> UnaryOperator<T> buildUpdate(Entity entity);

    @Nonnull
    <T> Consumer<T> buildDelete(Entity entity);
}
