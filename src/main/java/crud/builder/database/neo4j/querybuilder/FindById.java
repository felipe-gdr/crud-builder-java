package crud.builder.database.neo4j.querybuilder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.lang.String.format;

@ParametersAreNonnullByDefault
public class FindById {
    private static final String QUERY_TEMPLATE =
            "MATCH (entity: %s {id: \"%s\"}) return entity;";

    private String id;
    private String label;

    @Nonnull
    public FindById label(String label) {
        this.label = label;

        return this;
    }

    @Nonnull
    public FindById id(String id) {
        this.id = id;

        return this;
    }

    public String get() {
        return format(QUERY_TEMPLATE, label, id);
    }
}
