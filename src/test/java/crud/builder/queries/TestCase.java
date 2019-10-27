package crud.builder.queries;

import java.util.List;
import java.util.Map;

public class TestCase {
    public String description;
    public String data;
    public String query;
    public Map<String, Object> expected;
    public List<String> errors;

    @Override
    public String toString() {
        return description != null ? description : query;
    }
}



