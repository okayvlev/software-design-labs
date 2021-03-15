import org.junit.Test

class FunctionalTests {

    @Test
    fun `single request`() = withServer(defaultRoutingConfig) {
        val query = "hello"
        val actual = getDefaultService().search(query)
        val expected = getFullExpectedResponse(query)

        assertEqualResults(expected, actual)
    }

    @Test
    fun `multiple requests`() = withServer(defaultRoutingConfig) {
        val query1 = "hello1"
        val query2 = "hello2"
        val service = getDefaultService()
        val actual1 = service.search(query1)
        val actual2 = service.search(query2)
        val expected1 = getFullExpectedResponse(query1)
        val expected2 = getFullExpectedResponse(query2)

        assertEqualResults(expected1, actual1)
        assertEqualResults(expected2, actual2)
    }

}