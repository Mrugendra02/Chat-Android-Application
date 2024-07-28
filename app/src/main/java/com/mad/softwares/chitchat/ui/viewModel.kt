package com.mad.softwares.chitchat.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.Timestamp
import com.mad.softwares.chitchat.MyApplication
import com.mad.softwares.chitchat.data.Chats
import com.mad.softwares.chitchat.data.DataRepository
import com.mad.softwares.chitchat.data.MessageReceived
import com.mad.softwares.chitchat.data.User
import com.mad.softwares.chitchat.data.uiState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

val TAG = "ViewModel_Tags"

//enum class isChatButton(){
//    Loading,
//    Yes,
//    No
//}
enum class isPasswordReset(){
    maybe,
    Yes,
    No
}

enum class isUsernamesLoading(){
    Loading,
    Success,
    Failed
}

enum class isChatsLoading(){
    Loading,
    Success,
    Failed
}

//@SuppressLint("StaticFieldLeak")
class chitChatViewModel(
    private val dataRepository: DataRepository,
): ViewModel(){

    private var _uiState = MutableStateFlow(uiState())
        private set

    var extUiState : StateFlow<uiState> = _uiState.asStateFlow()
    private fun getFCMTokenAndUser() {
        Log.d(TAG,"Inside teh getfcmtoken")
        _uiState.update { cr->cr.copy(
            isLoading = true
        ) }
        viewModelScope.launch{
            val ton = dataRepository.getToken()
            _uiState.update { current -> current.copy(fcmToken = ton ?: "Null token") }
            getUserFromToken(ton ?: "Blank")
//            if(ton!=""){
//                Log.d(TAG, "Token is $ton")
//                _uiState.update { current -> current.copy(fcmToken = ton ?: "Null token") }
//                getUserFromToken(ton ?: "Blank")
//            }
//            else{
//                Log.e(TAG,"Error token is : $ton")
//                Log.e(TAG,"Error to get token properly should no go anywhere")
////                getFCMTokenAndUser()
//            }
        }

    }

    init {
        getFCMTokenAndUser()
    }

    fun updateUsername(username:String){
        if(username.length<=16){
            _uiState.update { current ->
                current.copy(
                    userName = username,
                    userNameExist = false
                )
            }
        }
    }

    fun loginUsername(username: String){
        if(username.length<=16){
            _uiState.update { current ->
                current.copy(
                    userName = username,
                    userNameExist = true
                )
            }
        }
    }

    fun updatePassword(password:String){
        if(password.length<=16){
            _uiState.update { current ->
                current.copy(
                    password = password
                )
            }
        }
    }

    private fun generateSixDigitUUID(n:Int): String {
        val randomUUID = UUID.randomUUID()
        val hashCode = Math.abs(randomUUID.hashCode()).toString()
        return hashCode.take(n).padStart(6, '0')
    }

    private fun CheckConditionUsername(
        username:String = _uiState.value.userName,
        password: String = _uiState.value.password
    ):Boolean {
        return username == ""
    }

    private fun CheckConditionPassword(
        password: String = _uiState.value.password
    ):Boolean {
        return password == ""
    }

    fun registerUser(){
        if(CheckConditionUsername()){
            _uiState.update { it.copy(
                emptyUsername = true
            ) }
            if(CheckConditionPassword()){
                _uiState.update { it.copy(
                    emptyPassword = true
                ) }
            }
            return
        }
        viewModelScope.launch{

            Log.d(TAG,"registarion called")
            _uiState.update { cr->cr.copy(
                isLoading = true
            ) }
            if (!dataRepository.usernameExist(_uiState.value.userName)) {
//                    delay(Random.nextLong(2000, 5001))
                    _uiState.update { cr -> cr.copy(userNameExist = true,
                        isLoading = false) }
                    Log.d(TAG, "user Exist in db : ${_uiState.value.userNameExist}")

//                throw Exception()
//                return@launch
            }
            else{
                _uiState.update { cr->cr.copy(
                    uniqueAuth = generateSixDigitUUID(8),
                    isLoading = true
                ) }
                try{
                    Log.d(TAG,"Registerd the user")
                   val docId: Deferred<String> =async {
                       dataRepository.registerUser(
                           fcmToken = _uiState.value.fcmToken,
                           password = _uiState.value.password,
                           profilePic = _uiState.value.profilePic,
                           uniqueId = _uiState.value.uniqueAuth,
                           username = _uiState.value.userName,
                       )
                   }
//                        docId.await()
                    val newMyUserData:User = _uiState.value.myUserData.copy(
                        fcmToken = _uiState.value.fcmToken,
                        password = _uiState.value.password,
                        uniqueId = _uiState.value.uniqueAuth,
                        username = _uiState.value.userName,
                        profilePic = _uiState.value.profilePic,
                        docId = docId.await()
                    )
                    Log.d(TAG,"User regs in viewmodel is : $newMyUserData")
                    _uiState.update { cr->cr.copy(
                        passIsSuccess = true,
                        isLoading = false,
                        regsSuccess = true,
//                            isChatsButtonEnabled = isChatButton.Yes,
                        myUserData = newMyUserData
                    ) }
                    Log.d(TAG,"myUserData is : ${_uiState.value.myUserData}")
                }
                catch(e:Exception){
                    Log.d(TAG,"unable to upload from viewmodel")
                    _uiState.update { cr->cr.copy(
                        passIsSuccess = false
                    ) }
//                    throw e
                }
            }
        }
    }

    fun registrationAuthenticated(){
        _uiState.update { cr->cr.copy(regsSuccess = false) }
    }

    fun loginAuthenticated(){
        _uiState.update { cr->cr.copy(
            loginSuccess = false
        ) }
    }

    fun userExistCheck(){
        viewModelScope.launch{
//            delay(Random.nextLong(2000, 5001))
            val exist = async{ dataRepository.usernameExist(_uiState.value.userName) }
            exist.await()
            Log.d(TAG,"check called")
        }
    }

    fun loginUser(){
        Log.d(TAG,"login viewmodel called")
        _uiState.update { cr->cr.copy(
            loginSuccess = false,
            isLoading = true,
            uniqueAuth = ""
        ) }
        viewModelScope.launch{
//            delay(Random.nextLong(2000, 5001))
            if(!dataRepository.usernameExist(_uiState.value.userName)) {
                val logSucc: User = try {
                    val login: Deferred<User> = async{dataRepository.loginUser(
                        username = _uiState.value.userName,
                        password = _uiState.value.password,
//                        _uiState.value.fcmToken,
                        currFcmToken = "",
                        uniqueId = ""
                    )}
                    login.await()
                } catch (e: Exception) {
                    Log.d(TAG,"Error to login ")
                    _uiState.update { cr ->
                        cr.copy(
                            loginSuccess = false,
                            isLoading = false,
                            overAllError = true
                        )
                    }
                    User(fcmToken = "not valid")
                }
                Log.d(TAG,"$logSucc success to login")
                if(logSucc.fcmToken!="not valid"){

//                    tempMyUserData = logSucc

                    _uiState.update { cr ->
                        cr.copy(
                            loginSuccess = true,
                            isLoading = false,
//                            myTempUserData = logSucc
                            myUserData = logSucc
                        )
                    }
                }
                else{
                    _uiState.update { cr->cr.copy(
                        passFound = false,
                        isLoading = false
                    ) }
                }
            }
            else{
                Log.d(TAG,"Login doesnot exist")
                _uiState.update { cr->cr.copy(
                    userNameExist = false,
                    isLoading = false
                ) }
            }
        }
    }

    fun updateForSignUp(){
        _uiState.update { cr->cr.copy(
            userName = "",
            password = "",
            userNameExist = false
        ) }
    }

    fun updateForLogin(){
        _uiState.update { cr->cr.copy(
            userName = "",
            password = "",
            userNameExist = true
        ) }
    }

    fun handelAuthTextField(userId:String){
        if (userId.length<=8){
            _uiState.update { cr ->
                cr.copy(
                    uniqueAuth = userId
                )
            }
        }
    }

    fun authenticateUserByUserId(){
        _uiState.update { cr ->
            cr.copy(
                isAuthenticated = false,
                navAuthenticate = false,
//                wrongOtp = false,
                isLoading = true,
//                isChatsButtonEnabled = isChatButton.No
            )
        }
        viewModelScope.launch{
//            delay(Random.nextLong(2000,5000))
            Log.d(TAG,"Authentication started")
            if (dataRepository.verifyUserByUniqueId(_uiState.value.uniqueAuth)) {
                dataRepository.loginUser(
                    username = _uiState.value.userName,
                    password = _uiState.value.password,
                    currFcmToken = _uiState.value.fcmToken,
                    uniqueId = _uiState.value.uniqueAuth
                )
                _uiState.update { cr ->
                    cr.copy(
                        isAuthenticated = true,
                        navAuthenticate = true,
                        wrongOtp = false,
                        isLoading = false,
                        myUserData = _uiState.value.myUserData
//                        isChatsButtonEnabled = isChatButton.Yes
                    )
                }
                Log.d(TAG,"Authentication Complete")
            }
            else{
                _uiState.update { cr ->
                    cr.copy(
                        isAuthenticated = false,
                        navAuthenticate = false,
                        wrongOtp = true,
                        isLoading = false,
//                        isChatsButtonEnabled = isChatButton.Yes
                    )
                }
            }
        }
    }

    fun authenSuccess(){
        _uiState.update { cr->cr.copy(
            navAuthenticate = false,
            isChatsButtonEnabled = ChatScreens.AllChats
        ) }
    }

    private fun getUserFromToken(
        fcmToken:String
    ){
        viewModelScope.launch{
//            delay(2)
            _uiState.update { cr->cr.copy(
                isLoading = true
            ) }
            Log.d(TAG,"token in get user : ${fcmToken}")
            val user = dataRepository.getUserDataUsingtoken(fcmToken)
            if(user.docId!=""){
                Log.d(TAG,"Device registered already")
                _uiState.update { cr ->
                    cr.copy(
                        myUserData = user,
                        isChatsButtonEnabled = ChatScreens.AllChats,
                        isLoading = false,
                        userName = user.username,
//                        isMyChatsLoading = isChatsLoading.Loading
                    )
                }
                getAllAvailableChats(false)
            }
            else{
                _uiState.update { cr ->
                    cr.copy(
                        isChatsButtonEnabled = ChatScreens.Start,
                        isLoading = false
                    )
                }
                Log.d(TAG,"device is not registered")
            }
        }
    }

    fun logoutUser(){
        viewModelScope.launch{
            _uiState.update {cr->cr.copy(
                isLoading = true
            )
            }
            Log.d(TAG,"logout Started")
            try {

                dataRepository.logoutUser(_uiState.value.myUserData)
                _uiState.update {cr->cr.copy(
                    isLoading = false,
                    isChatsButtonEnabled = ChatScreens.Start
                )
                }
                Log.d(TAG,"Logout Success full ")
            } catch (e: Exception) {
                Log.d(TAG, "unable to logout from viewmodel")
                _uiState.update {cr->cr.copy(
                    isLoading = false,
                    isChatsButtonEnabled = ChatScreens.Start,
                    overAllError = true
                )
                }
            }
        }
    }

    fun resetPassword(){
        Log.d(TAG,"Reset started")
       viewModelScope.launch {
           _uiState.update {cr->cr.copy(
               isLoading = true
           )
           }
            try {
                val repas = async{
                    dataRepository.resetDataPassword(
                        _uiState.value.userName,
                        _uiState.value.password,
                        _uiState.value.uniqueAuth
                    )
                }

                if(repas.await()){
                    Log.d(TAG,"Reset success")
                    _uiState.update { cr ->
                        cr.copy(
                            isPasswordResetSuccess = isPasswordReset.Yes,
                            isLoading = false,
                            userNameExist = true
                        )
                    }
                }else{
                    Log.d(TAG,"Reset Failed")
                    _uiState.update { cr ->
                        cr.copy(
                            isPasswordResetSuccess = isPasswordReset.No,
                            isLoading = false
                        )
                }
                }
            }
            catch (e:Exception){
                _uiState.update { cr->cr.copy(
                    isPasswordResetSuccess = isPasswordReset.No,
                    isLoading = false
                ) }
            }
        }
    }

    fun passResetSuccess(){
        _uiState.update { cr->cr.copy(
            userName = "",
            password = "",
            uniqueAuth = "",
            isPasswordResetSuccess = isPasswordReset.maybe,
            isLoading = false
        ) }
    }

    fun updateAllTheUsers(isForced:Boolean = false){
        if(_uiState.value.isUsersLoading == isUsernamesLoading.Loading || _uiState.value.isUsersLoading == isUsernamesLoading.Failed || isForced == true)
        {
            Log.d(TAG, "Update user trigged")
            _uiState.update {
                it.copy(
//                    isLoading = true,
                    isRefreshing = true,
                    isUsersLoading = isUsernamesLoading.Loading
                )
            }
            viewModelScope.launch {
                delay(2000)
                try {
                    val allUsers = async { dataRepository.getAllTheUsers(_uiState.value.userName,_uiState.value.allAvailableChats.map { it.chatName }) }
                    _uiState.update {
                        it.copy(
                            allAvailableUsers = allUsers.await(),
                            isLoading = false,
                            isRefreshing = false,
                            isUsersLoading = isUsernamesLoading.Success
                        )
                    }
                    Log.d(TAG, "got the users")
                } catch (e: Exception) {
                    Log.d(TAG, "unable to get the users")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            overAllError = true,
                            isRefreshing = false,
                            isUsersLoading = isUsernamesLoading.Failed
                        )
                    }
                }
            }
        }
    }

    fun createNewChat(user:String){
        _uiState.update {
            it.copy(
                members = _uiState.value.members + user,
                isLoading = true,
            )
        }
        Log.d(TAG,"Chat creation Started in viewmodel")
        Log.d(TAG,"members are ${_uiState.value.members}")
        val chatsId = generateSixDigitUUID(11)
        viewModelScope.launch{
            try{
                dataRepository.addChatToDatabase(
                    currentUser = _uiState.value.myUserData,
                    memberUsers = _uiState.value.members,
                    chatName = chatsId,
                    chatId = chatsId,
                    profilePhoto = _uiState.value.groupPic,
                    isGroup = false
                )
                _uiState.update { it.copy(
                    members = listOf(),
                    isLoading = false
                ) }
                Log.d(TAG,"Added chat viewModel")
            }
            catch(e:Exception){
                Log.d(TAG,"Unable to add chat")
                _uiState.update { it.copy(
                    members = listOf(),
                    isLoading = false
                ) }
            }
        }
    }

    fun getAllAvailableChats(isForced: Boolean){
        if(_uiState.value.isMyChatsLoading == isChatsLoading.Loading || _uiState.value.isMyChatsLoading == isChatsLoading.Failed || isForced)
        {
            _uiState.update {
                it.copy(
                    isRefreshing = true,
                    isMyChatsLoading = isChatsLoading.Loading
                )
            }
            Log.d(TAG, "Chats fetching in viewmodel")
//            Log.d(TAG,"current usr ${_uiState.value.myUserData}")
            viewModelScope.launch {
                try {
                    Log.d(TAG,"reached at 1")
//                    delay(2000)
                    val myChats =
                        async { dataRepository.getChats(_uiState.value.myUserData.username) }
                    _uiState.update {
                        it.copy(
                            allAvailableChats = myChats.await(),
                            isRefreshing = false,
                            isMyChatsLoading = isChatsLoading.Success
                        )
                    }
                    Log.d(TAG,"chats are : ${myChats.await()}")
                    Log.d(TAG,"reached at 2")
//                    _uiState.update {it.copy(
//                        members = _uiState.value.allAvailableChats.map { it.chatName }
//                    )
//                    }
                } catch (e: Exception) {
                    Log.e(TAG, "unable to get all the chats : $e")
                    _uiState.update {
                        it.copy(
//                            allAvailableChats = myChats.await(),
                            isRefreshing = false,
                            isMyChatsLoading = isChatsLoading.Failed
                        )
                    }
                }

            }
        }
    }

    fun setCurrentChatId(currentChat:Chats){
        _uiState.update {it.copy(
            isLoading = true
        )
        }
        _uiState.update {it.copy(
            currentChat = currentChat,
            isLoading = false
        )
        }
        Log.d(TAG,"current chatid : ${_uiState.value.currentChat.chatId}")
        getTokens()
        getMessages()
    }

    private fun getTokens(){
        _uiState.update {it.copy(
            isLoading = true
        )
        }
        viewModelScope.launch {
            try {
                val tokens =
                    async { dataRepository.getTokenForMemebers(
                        _uiState.value.currentChat.members,
                        _uiState.value.myUserData.fcmToken
                    ) }
//                tokens.await()
                _uiState.update {it.copy(
                    listOfMembersForToken = tokens.await(),
                    isLoading = false
                )
                }
                Log.d(TAG,"got tokens ${_uiState.value.listOfMembersForToken}")
            }
            catch (e:Exception){
                Log.d(TAG,"unable to get the tokens")
                _uiState.update {it.copy(
                    isLoading = false,
                    overAllError = true
                )
                }
            }
        }

    }

    fun getMessages(){

        _uiState.update {it.copy(
            isLoading = true,
            isRefreshing = true
        )
        }
        try { viewModelScope.launch {
            val mess = async{ dataRepository.getMyMessages(_uiState.value.currentChat.chatId) }

            _uiState.update { it.copy(
                messagesForChat = mess.await(),
                isLoading = false,
                isRefreshing = false
            ) }
            Log.d(TAG,"Got messages successfully ${_uiState.value.messagesForChat}")
        } }catch (e:Exception){
            Log.d(TAG,"Failed to send message from viewmodel")
        }
    }

    fun updateMessageToSend(messge:String){
        _uiState.update { it.copy(
            messageToSend = messge,
            isLoading = false,
            isRefreshing = false
        ) }
    }

    fun sendMessageTo(){
        val newMessage = MessageReceived(
            content = _uiState.value.messageToSend,
            chatId = _uiState.value.currentChat.chatId,
            senderId = _uiState.value.myUserData.username,
            timeStamp = Timestamp.now()
        )

        viewModelScope.launch{
            try{
                dataRepository.sendMessage(newMessage)
                sendNotification(newMessage.content)
            Log.d(TAG,"send successfully message viewmode")
                _uiState.update { it.copy(
                    messageToSend = ""
                ) }

            }
            catch (e:Exception){
                Log.d(TAG,"unable to send message from viewmodel")
                _uiState.update { it.copy(
                    messageToSend = ""
                ) }
            }
        }
    }

    fun sendNotification(content: String){
        viewModelScope.launch{
            for(token in _uiState.value.listOfMembersForToken) {
                try {
                    Log.d(TAG, "send notifcaiton succdsff viewmodel to $token")
                    dataRepository.sendNotificationToToken(
                        token = token,
                        title = content,
                        content = _uiState.value.myUserData.username
                    )
                } catch (e: Exception) {
                    Log.d(TAG, "unable to send notification viewmodel")
                }
            }
        }
    }

    fun deleteChatUi(chatId:String){
        viewModelScope.launch {
            try {
                dataRepository.deleteChat(chatId = chatId)
                getAllAvailableChats(true)
            }
            catch (e:Exception){
                Log.e(TAG,"Error in viewmodel for delete chat")
            }
        }
    }

    companion object{
        val Factory:ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MyApplication)
                val tokenRepository = application.container.dataRepository
                chitChatViewModel(
                    dataRepository = tokenRepository
                )
            }
        }
    }
}