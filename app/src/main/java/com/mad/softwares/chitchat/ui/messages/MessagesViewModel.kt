package com.mad.softwares.chitchat.ui.messages

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mad.softwares.chitchat.data.Chats
import com.mad.softwares.chitchat.data.ContentType
import com.mad.softwares.chitchat.data.DataRepository
import com.mad.softwares.chitchat.data.MessageReceived
import com.mad.softwares.chitchat.data.messageStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val TAGmess = "messagesViewModel"

class MessagesViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository,
) : ViewModel() {
    var messagesUiState = MutableStateFlow(MessagesUiState())
        private set

    init {
        val chatID_username =
            savedStateHandle.get<String>(messagesdestinationData.chatIDAndUsername)
        val chatID = chatID_username?.split(",")?.get(0)
        val chatName = chatID_username?.split(",")?.get(1)
        messagesUiState.update {
            it.copy(
                chatID = chatID ?: "No Id here",
                chatName = chatName ?: "No username here"
            )
        }
        getChatInfo()
        getMessages()
    }

    fun getChatInfo() {
        messagesUiState.update {
            it.copy(
                messageScreen = MessageScreen.Loading
            )
        }
        try {
            viewModelScope.launch {
                val currChat: Deferred<Chats> =
                    async {
                        dataRepository.getDataChat(
                            chatId = messagesUiState.value.chatID,
                            chatName = messagesUiState.value.chatName
                        )
                    }
//                messagesUiState.update { it.copy(
//                    currChat = currChat.await(),
//                    messageScreen = MessageScreen.Success
//                ) }
                if (currChat.await().chatName == "") {
                    messagesUiState.update {
                        it.copy(
                            messageScreen = MessageScreen.Error,
                            isError = true,
                            errorMessage = currChat.await().chatPic
                        )
                    }
                    Log.e(TAGmess, "Unable to get the chat info")
                } else {
                    messagesUiState.update {
                        it.copy(
                            currChat = currChat.await(),
                            messageScreen = MessageScreen.Success
                        )
                    }
                    Log.d(TAGmess, "Got chat info success")
                }
            }
        } catch (e: Exception) {
            Log.e(TAGmess, "Unable to get the chat info : $e")
            messagesUiState.update {
                it.copy(
                    messageScreen = MessageScreen.Error,
                    isError = true,
                    errorMessage = e.message.toString()
                )
            }
        }
    }

    fun messageEdit(content: String) {
        messagesUiState.update {
            it.copy(
                messageToSend = content
            )
        }
    }

    private val viewModelJob = SupervisorJob()
    private val backgroundScope = CoroutineScope(Dispatchers.Default + viewModelJob)

    override fun onCleared() {
        super.onCleared()
//        viewModelJob.cancel()
    }

    fun sendTextMessage() {
        val sender: List<String> =
            messagesUiState.value.currChat.members - messagesUiState.value.currChat.membersData.map { it.username }
                .toSet()
        val senderUsername = sender.get(0)
        val newMessage = MessageReceived(
            contentType = ContentType.text,
            content = messagesUiState.value.messageToSend,
            senderId = senderUsername,
            status = messageStatus.Sending
        )
        messagesUiState.value.messages.add(
            newMessage
        )
        messagesUiState.update {
            it.copy(
                messageToSend = ""
            )
        }
        Log.d(TAGmess,"Message status = ${messagesUiState.value.messages.last().status}")
//        messagesUiState.value.messages.set(
//            index= messagesUiState.value.messages.indexOf(messagesUiState.value.messageToSend),
//            element = messagesUiState.value.messageToSend.copy(status = messageStatus.Send)
//        )
        backgroundScope.launch()  {
            Log.d(TAGmess, "Sending message")
//            delay(5000)
            try {

                dataRepository.sendMessage(
                    message = newMessage,
                    chatId = messagesUiState.value.chatID,
//                    chatId = "12345677"
                )


                messagesUiState.value.messages.set(
                    index = messagesUiState.value.messages.indexOf(newMessage),
                    element = newMessage.copy(status = messageStatus.Send)
                )
                Log.d(TAGmess, "Message status = ${messagesUiState.value.messages.last().status}")
                messagesUiState.update { it.copy(
                    errorMessage = "No error : ${newMessage.timeStamp}"
                ) }
                Log.d(TAGmess, "Message sent successfully ")
//                getMessages(true)

            } catch (e: Exception) {
                Log.e(TAGmess, "Unable to send the message : $e")
                try{
                    messagesUiState.value.messages.set(
                        index = messagesUiState.value.messages.indexOf(newMessage),
                        element = newMessage.copy(status = messageStatus.Error)
                    )
                }
                catch (e:Exception){
                    Log.e(TAGmess,"Unable to update the message status : $e")
                }
//                    Log.e(TAGmess,"Unable to send the message")
//                throw Exception("Unable to send the message : ${status.await()}")
                messagesUiState.update {
                    it.copy(
//                        messageScreen = MessageScreen.Error,
//                        isError = true,
                        errorMessage = "${e.message.toString()} : ${newMessage.timeStamp}"
                    )
                }
                Log.e(TAGmess,"Error sending message :$e")
            }
        }
    }

    fun getMessages(isForced: Boolean = false) {
        if (!isForced && messagesUiState.value.messages.isNotEmpty()) {
            return
        }
        messagesUiState.update {
            it.copy(
                messageScreen = MessageScreen.Loading
            )
        }
        viewModelScope.launch {
            try {
                val messages = async {
                    dataRepository.getMyMessages(
                        currentChatId = messagesUiState.value.chatID
                    )
                }
                if(messages.await().get(0).senderId == "ErrorSerious"){
//                    messagesUiState.update {
//                        it.copy(
//                            messageScreen = MessageScreen.Error,
//                            isError = true,
//                            errorMessage = messages.await().get(0).content)}
                    throw Exception(messages.await().get(0).content)
                }
                messagesUiState.update {
                    it.copy(
                        messages = messages.await().toMutableList(),
                        messageScreen = MessageScreen.Success
                    )
                }
                Log.d(TAGmess, "Got the messages success")
            } catch (e: Exception) {
                Log.e(TAGmess, "Unable to get the messages : $e")
                messagesUiState.update {
                    it.copy(
                        messageScreen = MessageScreen.Error,
                        isError = true,
                        errorMessage = e.message.toString()
                    )
                }
            }
        }
    }
}

data class MessagesUiState(
    val chatID: String = "",
    val currChat: Chats = Chats(),
    val chatName: String = "",
//    val isLoading:Boolean = false,
    val isError: Boolean = false,
    val messageScreen: MessageScreen = MessageScreen.Loading,
    val errorMessage: String = "",
    val messageToSend: String = "",
    val messages: MutableList<MessageReceived> = mutableListOf()
)

enum class MessageScreen() {
    Loading,
    Error,
    Success
}