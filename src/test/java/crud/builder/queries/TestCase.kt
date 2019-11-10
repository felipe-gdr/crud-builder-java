package crud.builder.queries

/**
 * In memory representation of a test case
 */
class TestCase {
    /**
     * A textual description of the test case
     */
    var description: String? = null
    /**
     * Path to the file containing the test data
     */
    var data: String? = null
    /**
     * The GraphQL query to be used during the test
     */
    var query: String? = null
    /**
     * The data that should be returned by the execution of the GraphQL query in order for
     * the test to be considered successful.
     * If this value is null or an empty map, the execution is expected to return no data (eg.: it should
     * return only "errors").
     * note: a test case may return both data and errors (ie: partial errors).
     */
    var expected: Map<String, Any>? = null
    /**
     * The list of errors that should be returned by the execution of the GraphQL query in order
     * for the test to be considered successful.
     * If this value is null or an empty list, the execution is expected to return no errors (eg.: it should
     * return only "data").
     * note: a test case may return both data and errors (ie: partial errors).
     */
    var errors: List<String>? = null

    override fun toString(): String {
        listOf("string")
        return if (description != null) requireNotNull(description) else requireNotNull(query)
    }
}



