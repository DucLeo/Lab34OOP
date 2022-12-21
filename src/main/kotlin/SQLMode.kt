import java.sql.Connection
import java.sql.DriverManager

class SQLMode : BotMode {
    private val connection: Connection

    init {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
        val connectionUrl = "jdbc:sqlserver://localhost:1433;database=Countries;trustServerCertificate=true;"
        connection = DriverManager.getConnection(connectionUrl, "sa", "ducngu108")
    }

    override fun allCountriesBeginWithLetter(letter: Char): List<String> {
        val request = connection.createStatement().executeQuery("""
            select Country from Countries
            where Country like '${letter}%'
        """.trimIndent())

        val result = mutableListOf<String>()

        while (request.next()) {
            result.add(request.getString("Country"))
        }
        return result.toList()
    }

    override fun allCountriesLeft(letter: Char): List<String> {
        val request = connection.createStatement().executeQuery("""
            select Country from Countries
            where Country like '${letter}%' and Selected = 0
        """.trimIndent())

        val result = mutableListOf<String>()

        while (request.next()) {
            result.add(request.getString("Country"))
        }
        return result.toList()
    }

    override fun chooseCountry(letter: Char): String? {
        val request = connection.createStatement().executeQuery("""
            select Country from Countries
            where Country like '${letter}%' and Selected = 0 
        """.trimIndent())
        return if (request.next())
            request.getString("Country")
        else null
    }

    override fun clearSelected() {
        connection.createStatement().executeQuery("""
            update Countries
            set Selected = 0
        """.trimIndent())
    }

    override fun addToSelected(country: String) {
        connection.createStatement().executeQuery("""
            update Countries
            set Selected = 1
            where Country = '$country'
        """.trimIndent())
    }

    override fun checkEnd(letter: Char): Boolean {
        val request = connection.createStatement().executeQuery("""
            select Country from Countries
            where Country like '${letter}%' and Selected = 0
        """.trimIndent())
        return request.next()
    }

    override fun checkRightName(country: String): Boolean {
        val request = connection.createStatement().executeQuery("""
            select Country from Countries
            where Country = '$country'
        """.trimIndent())
        return request.next()
    }

    override fun checkSelected(country: String): Boolean {
        val request = connection.createStatement().executeQuery("""
            select Country from Countries
            where Country = $country and Selected = 1
        """.trimIndent())
        return request.next()
    }
}