import java.io.File
import java.io.InputStream

class NormalMode : BotMode {
    private val countries = mutableListOf<String>()
    private val selected = mutableListOf<String>()
    private val input: InputStream = File("C:\\Users\\GodLu\\IdeaProjects\\Lab34OOP\\src\\main\\resources\\Countries.txt").inputStream()

    init {
        input.bufferedReader().forEachLine { countries.add(it) }
    }

    override fun allCountriesBeginWithLetter(letter: Char) : List<String> {
        val listCountries = mutableListOf<String>()
        countries.forEach {
            if (it[0] == letter) listCountries.add(it)
        }
        return listCountries.toList()
    }

    override fun allCountriesLeft(letter: Char) : List<String> {
        val result = mutableListOf<String>()
        val listCountries = allCountriesBeginWithLetter(letter)
        listCountries.forEach {
            if (!selected.contains(it))
                result.add(it)
        }
        return result.toList()
    }

    override fun chooseCountry(letter: Char) : String? {
        val listCountries = allCountriesBeginWithLetter(letter)
        return if (selected.containsAll(listCountries))
            null
        else {
            var result: String
            do {
                result = listCountries.random()
            } while (selected.contains(result))
            result
        }
    }

    override fun clearSelected() {
        selected.clear()
    }

    override fun addToSelected(country: String) {
        selected.add(country)
    }

    override fun checkEnd(letter: Char) : Boolean {
        return selected.containsAll(allCountriesBeginWithLetter(letter))
    }

    override fun checkRightName(country: String): Boolean {
        return countries.contains(country)
    }

    override fun checkSelected(country: String): Boolean {
        return selected.contains(country)
    }
}