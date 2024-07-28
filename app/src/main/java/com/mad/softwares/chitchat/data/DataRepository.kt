package com.mad.softwares.chitchat.data

import android.util.Log
import com.mad.softwares.chitchat.network.FirebaseApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

val TAG="DataRepository_Logs"
interface DataRepository {
    suspend fun getToken():String?
    suspend fun usernameExist(username:String):Boolean
    suspend fun registerUser(
        fcmToken:String,
        password:String,
        profilePic:String,
        uniqueId:String,
        username:String
    ):String

    suspend fun loginUser(
        username: String,
        password: String,
        currFcmToken: String,
        uniqueId:String
    ):User

    suspend fun verifyUserByUniqueId(
        uniqueId: String
    ):Boolean

    suspend fun getUserDataUsingtoken(Token:String):User

//    suspend fun updateUser(
//        fcmToken:String,
//        password:String,
//        profilePic:String,
//        uniqueId:String,
//        username:String,
//        docId:String
//    )

    suspend fun logoutUser(currUser:User)

    suspend fun resetDataPassword(username: String,newPassword: String,uniqueId: String):Boolean

    suspend fun getAllTheUsers(username: String,members:List<String>):List<String>

    suspend fun addChatToDatabase(
        currentUser: User,
        memberUsers: List<String>,
        chatName: String,
        chatId: String,
        profilePhoto: String,
        isGroup: Boolean
    )

    suspend fun getChats(myUsername:String):List<Chats>

    suspend fun sendMessage(message:MessageReceived)

    suspend fun sendNotificationToToken(token:String,title:String,content:String)

    suspend fun getTokenForMemebers(members:List<String>,currentFcmToken:String):List<String>

    suspend fun getMyMessages(currentChatId:String):List<MessageReceived>

    suspend fun deleteChat(chatId: String)
}

class NetworkDataRepository(
    private val apiService: FirebaseApi
):DataRepository{
    override suspend fun getToken(): String? {
        Log.d(TAG,"Token in data started")
        val token: String = coroutineScope {
            val token = async { apiService.getFCMToken() }
            token.await()
        }
        Log.d(TAG, "Token from network is $token")
        return token
    }

    override suspend fun usernameExist(username: String): Boolean {

        val exist:Boolean =  coroutineScope {
            val exist = async{apiService.checkUsernameExist(username)}
            exist.await()
        }
        return exist
    }

    override suspend fun registerUser(
        fcmToken:String,
        password:String,
        profilePic:String,
        uniqueId:String,
        username:String
    ):String{
        val docId:String = try{
            coroutineScope{
                val regs = async{
                    apiService.registerUserToDatabase(
                        fcmToken, password, profilePic, uniqueId, username
                    )
                }
                regs.await()
            }
        }
        catch (e:Exception){
            Log.d(TAG,"throw error in datarepo user regw")
            throw e
        }
        Log.d(TAG,"Doc id for register in data is : $docId" )
       return docId
    }

    override suspend fun loginUser(
        username: String,
        password: String,
        currFcmToken: String,
        uniqueId: String
    ):User {
        val loginUsers= coroutineScope{
            val logSuc = async{ apiService.loginUserAndPasswordSuccess(username, password) }
            logSuc.await()
        }
//        for(user in loginUsers){
//            if(user.fcmToken==""){
//                Log.d(TAG,"data login for ${user.username}")
//                val userr = user.copy(fcmToken = currFcmToken)
//                val newUser = coroutineScope {
//                    val logSuc = async { apiService.updateUserToDatabase(currUser = userr) }
//                    logSuc.await()
//                }
//                 return newUser
//            }
//            else if(user.fcmToken == "not valid"){
//                return user
//            }
//        }
//        val user = loginUsers[0].copy(fcmToken = currFcmToken)
//
//        val userr = coroutineScope {
//            val logsuc = async { apiService.registerUserToDatabase(currUser = user) }
//            logsuc.await()
//        }

        return  if(currFcmToken == "") {
            Log.d(TAG,"User login in data started")
            loginUsers[0]
        } else{
            Log.d(TAG,"user auth started in data")
            val newUser = coroutineScope {
                val logSuc = async { apiService.loginUserToDatabase(
                    currUser = loginUsers[0],
                    currFcmToken=currFcmToken,
                    uniqueId = uniqueId
                ) }
                logSuc.await()
            }
             newUser
        }
    }

    override suspend fun verifyUserByUniqueId(uniqueId: String): Boolean {
        val authSuccess:Boolean = coroutineScope{
            val logSuc = async{ !apiService.authenticateWithUniqueId(uniqueId) }
            logSuc.await()
        }
        return authSuccess
    }

    override suspend fun getUserDataUsingtoken(Token: String): User {
        val userData = coroutineScope {
            val data = async { apiService.getUserFromTokenInDatabase(Token) }
            data.await()
        }
        return userData
    }

//    override suspend fun updateUser(
//        fcmToken: String,
//        password: String,
//        profilePic: String,
//        uniqueId: String,
//        username: String,
//        docId: String
//    ){
//        try{
//           coroutineScope{
//                val regs = async{
//                    apiService.updateUserToDatabase(
//                        fcmToken, password, profilePic, uniqueId, username,docId
//                    )
//                }
//                regs.await()
//            }
//        }
//        catch (e:Exception){
//            Log.d(TAG,"throw error in datarepo user regw")
//            throw e
//        }
//
//    }

    override suspend fun logoutUser(currUser: User) {
        try{
            Log.d(TAG,"Logout startd in data for usr : $currUser")
//            val curr = currUser.copy(fcmToken = "")
            apiService.logoutUser(currUser)
            Log.d(TAG,"Logout successful")
        }
        catch (e:Exception){
            Log.d(TAG,"unable to logout from datarepo")
        }
    }

    override suspend fun resetDataPassword(username: String,newPassword: String,uniqueId: String): Boolean {
        return try {
            Log.d(TAG,"updateing user id and pass is :${username} , ${newPassword} , ${uniqueId}")
            apiService.resetPassword(username = username,newPassword = newPassword,
                uniqueId = uniqueId
            ).isNotEmpty()
        }
        catch (e:Exception){
            false
        }
    }

    override suspend fun getAllTheUsers(username: String, members: List<String>): List<String> {
        val Users:List<User> = try{ apiService.getAllUsers(username) }
        catch (e:Exception){
            listOf<User>()
        }

        return Users.distinctBy { it.username }.sortedBy { it.username.lowercase() }.map { it.username } - members
    }

    override suspend fun addChatToDatabase(
        currentUser: User,
        memberUsers: List<String>,
        chatName: String,
        chatId: String,
        profilePhoto: String,
        isGroup: Boolean
    ){
        val newMembers = mutableListOf<String>(currentUser.username)

        for(user in memberUsers){
            newMembers.add(user)
        }
        Log.d(TAG,"Chat addition startd at data")

        try{
            apiService.createNewChat(
                members = newMembers,
                chatName = chatName,
                chatId = chatId,
                profilePhoto = profilePhoto,
                isGroup = isGroup
            )
            Log.d(TAG,"Successfully added chat")
        }catch (e:Exception){
            Log.d(TAG,"Failed to add chat at data")
            throw e
        }

    }

    override suspend fun getChats(myUsername: String): List<Chats> {
        val chatss = coroutineScope{
            Log.d(TAG,"Fetched chats in data")
            val chats = async{
                apiService.getChatsforMe(myUsername)
            }
            chats.await()
        }

        val myChats = chatss.map { t->
            var tempUsername:String = "chat"
            for(mem in t.members){
                if(mem != myUsername){
                    tempUsername = mem
                }
            }
            if(t.isGroup == false){
                t.copy(chatName = tempUsername)
            }
            else{
                t
            }
        }
        Log.d(TAG,"Chats are : ${myChats}")

        return myChats
    }

    override suspend fun sendMessage(message: MessageReceived) {
        try {
            Log.d(TAG,"send successfully from data")
            apiService.sendNewMessage(message)
        }catch (e:Exception){
            Log.d(TAG,"unabe to send to database from data")
            throw e
        }
    }

    override suspend fun sendNotificationToToken(token: String, title: String, content: String) {
        try {
            apiService.sendNotificationApi(token = token,title =title,body = content)
            Log.d(TAG,"Send notification  Successfully from data")
        }catch (e:Exception){
            Log.d(TAG,"Unable to send notification")
            throw e
        }
    }

    override suspend fun getTokenForMemebers(members: List<String>, currentFcmToken: String): List<String> {
        val tokenList = coroutineScope {
            val tokens = async { apiService.getTokenForMemebers(members) }
            tokens.await()
        }
        return tokenList - currentFcmToken
    }

    override suspend fun getMyMessages(currentChatId: String): List<MessageReceived> {
        val messages = coroutineScope{
            val mess = async { apiService.getMessagesForChat(currentChatId) }
            mess.await()
        }
        return messages.sortedBy { t->t.timeStamp }
    }

    override suspend fun deleteChat(chatId: String) {
        try{
            apiService.deleteChat(chatId)
        }
        catch (e:Exception){
            Log.d(TAG,"Error in data delete chatId: $chatId : $e")
        }
    }
}