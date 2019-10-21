package crud.builder.graphql;

import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.apache.commons.lang3.text.WordUtils.capitalize;

@ParametersAreNonnullByDefault
public final class Utils {

    @Nonnull
    public static String toTypeName(String entityName) {
        return WordUtils.capitalize(entityName);
    }

    @Nonnull
    public static String createMutationName(String entityName) {
        return "add" + capitalize(entityName);
    }
}
