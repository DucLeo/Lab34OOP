import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import kotlinx.coroutines.flow.first

fun getBotMode() : BotMode {
    while (true) {
        println("Choose mode: 1 - Normal Mode; 2 - SQL Mode")
        print("Your choice: ")
        when (readln()) {
            "1" -> return NormalMode()
            "2" -> return SQLMode()
            else -> {
                println("Wrong input, try again.")
            }
        }
    }
}

suspend fun main() {
    val game: BotMode = getBotMode()
    var letter = ' '
    var turn = '0'
    var suggest = 3

    val bot = telegramBot("5859226444:AAH19uewqIURaqfRZ3O8RP-X7gMQNvUz2Vk")

    bot.buildBehaviourWithLongPolling {
        println(getMe())

        onCommand("hello") {
            Bot.message(this, it.chat, "Hi!!! Let's play a game about the names of countries.\n" +
                    "Enter /play to play. Enter /rules to read the rules of the game.")
        }

        onCommand("play") {
            Bot.status = Status.PLAY
            Bot.message(this, it.chat, "Enter a letter of the English alphabet to play.")
            game.clearSelected()
            /*game.addToSelected("Vietnam")
            if (game.checkRightName("Vietnam")) {
                Bot.message(this, it.chat, "VN vo dich")
            }
            if (game.checkSelected("Vietnam")) {
                Bot.message(this, it.chat, "VN number 1")
            }*/
        }

        onCommand("rules") {
            Bot.message(this, it.chat,
                "To start playing you enter a letter of the English alphabet. " +
                        "We will list the countries that start with the letter you have chosen one by one. " +
                        "Then you choose 1 or 2. If you choose 1 you will go first, if you choose 2 I will go first. " +
                        "Whoever can tell the last country wins. " +
                        "You can use hints by typing /suggest. " +
                        "You can also stop the game by entering the command /quit."
            )
        }

        onText {
            val text = waitText { null }.first().text
            if (letter == ' ' && Bot.status == Status.PLAY && text != "/play") {
                letter = text[0].uppercaseChar()
                Bot.message(this, it.chat, "Let's find countries begin with letter ${letter}.\n" +
                        "Enter 1 if you want to go first. Enter 2 if you want to me to go first.")
            }
            else {
                if (turn == '0' && Bot.status == Status.PLAY && text != "/play") {
                    turn = text[0]
                    if (turn == '1')
                        Bot.message(this, it.chat, "OK. You go first.")
                    else if (turn == '2') {
                        Bot.message(this, it.chat, "OK. I go first.")
                        val choice = game.chooseCountry(letter)
                        if (choice == null) {
                            Bot.status = Status.MENU
                            Bot.message(
                                this,
                                it.chat,
                                "No more countries begin with letter ${letter}. You win! Congratulation!\n" +
                                        "Enter /play to play again. Enter /rules to read the rules of the game."
                            )
                            game.clearSelected()
                            letter = ' '
                            turn = '0'
                            suggest = 3
                        }
                        else {
                            game.addToSelected(choice)
                            Bot.message(this, it.chat, choice)
                            if (game.checkEnd(letter)) {
                                Bot.message(
                                    this,
                                    it.chat,
                                    "This is the last country begin with letter ${letter}. You lose :(\n" +
                                            "Enter /play to play again. Enter /rules to read the rules of the game."
                                )
                                Bot.status = Status.MENU
                                game.clearSelected()
                                letter = ' '
                                turn = '0'
                                suggest = 3
                            }
                        }
                    }
                    else {
                        Bot.message(this, it.chat, "Wrong input!! Enter again.")
                        letter = ' '
                        turn = '0'
                    }
                }
                else {
                    if (text in listOf("/play", "/rules", "/start")) return@onText
                    if (text == "/quit") {
                        Bot.message(this, it.chat, "There are still countries that start with the letter ${letter}, " +
                                "for example ${game.allCountriesLeft(letter)[0]}.\nGood luck next time!\n" +
                                "Enter /play to play again. Enter /rules to read the rules of the game.")
                        Bot.status = Status.MENU
                        game.clearSelected()
                        letter = ' '
                        turn = '0'
                        suggest = 3
                    }
                    else if (text == "/suggest") {
                        if (suggest > 0) {
                            suggest -= 1
                            Bot.message(this, it.chat, "${game.allCountriesLeft(letter)[0]}.\n" +
                                    "You have $suggest suggestions left.")
                        }
                        else {
                            Bot.message(this, it.chat,"No more suggestions left.")
                        }
                    }
                    else if (text[0] != letter) {
                        if (text[0].uppercaseChar() == letter)
                            Bot.message(this, it.chat, "Country names should be capitalized. Try again.")
                        else
                            Bot.message(this, it.chat, "Country name must start with the letter ${letter}. Try again.")
                    }
                    else if (!game.checkRightName(text))
                        Bot.message(this, it.chat, "This country does not exists. Try again.")
                    else if (game.checkSelected(text))
                        Bot.message(this, it.chat, "This country was mentioned. Try again.")
                    else {
                        game.addToSelected(text)
                        val choice = game.chooseCountry(letter)
                        if (choice == null) {
                            Bot.status = Status.MENU
                            Bot.message(
                                this,
                                it.chat,
                                "No more countries begin with letter ${letter}. You win! Congratulation!\n" +
                                        "Enter /play to play again. Enter /rules to read the rules of the game."
                            )
                            game.clearSelected()
                            letter = ' '
                            turn = '0'
                            suggest = 3
                        }
                        else {
                            game.addToSelected(choice)
                            Bot.message(this, it.chat, choice)
                            if (game.checkEnd(letter)) {
                                Bot.message(
                                    this,
                                    it.chat,
                                    "This is the last country begin with letter ${letter}. You lose :(\n" +
                                            "Enter /play to play again. Enter /rules to read the rules of the game."
                                )
                                Bot.status = Status.MENU
                                game.clearSelected()
                                letter = ' '
                                turn = '0'
                                suggest = 3
                            }
                        }
                    }
                }
            }
        }
    }.join()
}