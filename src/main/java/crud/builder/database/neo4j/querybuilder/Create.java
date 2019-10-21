package crud.builder.database.neo4j.querybuilder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

import static crud.builder.database.neo4j.Utils.labelName;
import static crud.builder.database.neo4j.Utils.toNeo4JNodeValue;
import static java.lang.String.format;

@ParametersAreNonnullByDefault
public class Create {
    private static final String QUERY_TEMPLATE =
            "CREATE (entity:%s %s) RETURN entity";

    private String label;
    private Map<String, Object> value;

    public Create label(@Nonnull String label) {
        this.label = label;

        return this;
    }

    public Create value(@Nonnull Map<String, Object> value) {
        this.value = value;

        return this;
    }

    public String get() {
        return format(
                QUERY_TEMPLATE,
                labelName(label),
                toNeo4JNodeValue(value)
        );
    }
}
