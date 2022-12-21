import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.chat.Chat

enum class Status {
    MENU,
    PLAY
}

object Bot {
    var status : Status = Status.MENU
    suspend fun message(behaviourContext: BehaviourContext, chat: Chat, message: String) {
        behaviourContext.sendTextMessage(chat, message)
    }
}