package com.mad.softwares.chitchat.network

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.messaging.FirebaseMessaging
import com.mad.softwares.chitchat.data.Chats
import com.mad.softwares.chitchat.data.MessageReceived
import com.mad.softwares.chitchat.data.User
import com.mad.softwares.chitchat.data.chatUser
import com.mad.softwares.chitchat.data.lastMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine



val TAG = "ApiServiceLog"

interface FirebaseApi {
    suspend fun getFCMToken():String

    suspend fun authenticateWithUniqueId(uniqueId: String):Boolean
//    suspend fun getUserFromTokenInDatabase(Token:String):User
    suspend fun loginAndUpdateUserToDatabase(
        currUser:User,
        currFcmToken:String,
        docId: String
    ):User

    suspend fun registerUserToDatabase(
        currUser: User,
        docId:String
    ):User

    suspend fun logoutUser(
        currUser: User
    )

    suspend fun getUserFromUid(uid:String):User

    suspend fun resetPassword(
        username: String,
        uniqueId: String,
        newPassword:String
    ):List<String>

    suspend fun sendNotificationApi(token:String, title:String, body:String)

    suspend fun getAllUsers():List<chatUser>

    suspend fun createNewChat(
        members:List<String>,
        chatName:String,
        chatId:String,
        profilePhoto:String = "",
        isGroup:Boolean = false,
        )

    suspend fun getChatsforMe(username:String):List<Chats>

    suspend fun sendNewMessage(message:MessageReceived)

//    suspend fun getTokenForMemebers(members:List<String>):List<String>

    suspend fun getMessagesForChat(currentChatId: String):List<MessageReceived>

    suspend fun deleteChat(chatId:String)

    suspend fun getUserChatData(username:String):chatUser
}

class NetworkFirebaseApi(
    val db:FirebaseFirestore,
    val userCollection:CollectionReference,
    val chatsCollection:CollectionReference,
    val messagesCollection:CollectionReference,
):FirebaseApi{
    override suspend fun getFCMToken():String {
        return withContext(Dispatchers.IO) {
            return@withContext suspendCoroutine<String> {
                    continuation->
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                            continuation.resume("")
                            return@OnCompleteListener
                        }

                        val token = task.result
                        continuation.resume(token?:"Invalid")
                        Log.d(TAG, "token: $token")
                    })
                    .addOnFailureListener {
                        Log.e(TAG,"error to get fcm token $it")
                    }

            }
        }
    }


    override suspend fun authenticateWithUniqueId(uniqueId: String): Boolean {
        return try{
            val result = userCollection
                .whereEqualTo("uniqueId",uniqueId)
                .get(Source.SERVER)
                .await()
            Log.d(TAG,"${result.documents} login success")
            result.isEmpty
        }
        catch(e:Exception){
            Log.d(TAG,"Backend problem")
            false
        }
    }

    override suspend fun getUserFromUid(uid: String): User {
        val resultUser = userCollection
            .document(uid)
            .get()

        lateinit var user:User
        try{
            val doc = resultUser.await()
            val username = doc.getString("username")?:""
            val profilePic = doc.getString("profilePic")?:""
            val uniqueId = doc.getString("uniqueId")?:""
            val docId = doc.id

            user = User(
                username = username,
                profilePic = profilePic,
                uniqueId = uniqueId,
                docId = docId
            )

        }
        catch (e:Exception){
            Log.d(TAG,"Unable to get user from uid : $e")
        }

//        resultUser.await().getString("username")
        return user
    }

    override suspend fun loginAndUpdateUserToDatabase(
        currUser: User,
        currFcmToken: String,
        docId: String
    ):User{

        if (docId == "garbage"){
            Log.d(TAG,"Doc id is garbage")
            return User()
        }

        Log.d(TAG,"Update started in api ")
        val updateUser = userCollection
            .document(docId)
            .update("fcmTokens",FieldValue.arrayUnion(currFcmToken))

        try
        { updateUser.await() }
        catch (e:Exception){
            Log.d(TAG,"Unable to add te user : $e")
        }
        Log.d(TAG,"Update ended in the api")
        return User(
            fcmToken = currFcmToken,
            profilePic = currUser.profilePic,
            uniqueId = currUser.uniqueId,
            username = currUser.username,
            docId = currUser.docId
        )


    }

    override suspend fun registerUserToDatabase(
        currUser: User,
        docId: String
    ):User{
        Log.d(TAG,"updateCalled here")
//        val docId = mutableListOf<String>()
        val sub = "no ID"
        val newTokens:ArrayList<String> = arrayListOf(currUser.fcmToken)

        if (docId == "garbage"){
            Log.d(TAG,"Doc id is garbage")
            return User()
        }

        val user = hashMapOf(
            "fcmTokens" to newTokens,
//            "password" to currUser.password,
            "profilePic" to currUser.profilePic,
            "uniqueId" to currUser.uniqueId,
            "username" to currUser.username
        )

        val newUserId = userCollection
            .document(docId)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG,"registration success doc id : ${currUser.docId}")
//                docId.add(it.)
            }
            .addOnFailureListener{e->
                Log.d(TAG,"error adding because :" ,e)
                throw e
            }
        try {
            newUserId.await()
        }
        catch (e:Exception){
            Log.d(TAG,"Unable to add the user : $e")
        }

        val newUser:User = User(
            currUser.fcmToken,
            currUser.profilePic,
            currUser.uniqueId,
            currUser.username,
            docId = docId
        )
        Log.d(TAG,"User details are : ${newUserId.await().toString()}")
        return newUser

    }

    override suspend fun logoutUser(currUser: User){
        val fcmToken = getFCMToken()
        Log.d(TAG,"Logout started for userId: ${currUser.docId} with fcm: $fcmToken")
        val updateUser = userCollection
            .document(currUser.docId)
            .update("fcmTokens",FieldValue.arrayRemove(fcmToken))

        try
        {
            updateUser.await()
            Log.d(TAG,"Logout successful : ${updateUser.await()}")
        }
        catch (e:Exception){
            Log.d(TAG,"Unable to logout the user : $e")
            throw e
        }
    }

    override suspend fun resetPassword(
        username: String,
        uniqueId: String,
        newPassword: String
    ): List<String> {

        val list = mutableListOf<String>()
        Log.d(TAG,"reset Started api")
        try{

            val querySnapshot = userCollection
                .whereEqualTo("username", username)
                .whereEqualTo("uniqueId", uniqueId)
                .get()
//                .await()
            Log.d(TAG,"query : ${querySnapshot.await().documents}")
            for (document in querySnapshot.await()) {
                Log.d(TAG,"Updated user with id ${document.id}")
                userCollection
                    .document(document.id)
                    .update("password", newPassword,"fcmTokens", arrayListOf<String>())
                list.add(document.id)
            }
            Log.d(TAG,"reset Success api")
        }catch (e:Exception){
            Log.d(TAG,"reset failed api")
            return listOf()
        }

        return list
    }

    override suspend fun sendNotificationApi(token: String, title: String, body: String) {

        Log.d(TAG,"TOKEN IS $token")

        Log.d(TAG,"Notification trigger in api")

        val notificationRequest = NotificationRequest(
            to = token,
            notification = Notification(title, body)
        )

        try{
            val response = service.sendNotification(notificationRequest)

            Log.d(TAG,"Successfully send notification api ${response.toString()}")
        }catch (e:Exception){
            Log.d(TAG,"Failed to send ${e.message}")
        }
    }

    override suspend fun getAllUsers(): List<chatUser> {
        Log.d(TAG,"started getting the users")
        val users = mutableListOf<chatUser>()
        try{
            val allUsers = userCollection
//                .whereNotEqualTo("username", username)
                .get()
            for (doc in allUsers.await().documents) {
                val fcmToken = doc.getString("fcmToken") ?: ""

                val profilePic = doc.getString("profielPic") ?: ""
                val usern = doc.getString("username") ?: ""

//            val docId = doc.id
//            val user = User(fcmToken, profilePic, "uniqueId", usern, docId)
                val user = chatUser(username = usern, profilePic = profilePic, fcmToken = fcmToken)

                Log.d(TAG, "Got user with id : ${user.fcmToken}")
                users.add(user)
            }
        }
        catch (e:Exception){
            Log.e(TAG,"Unable to get the users : $e")
            throw e
        }
        Log.d(TAG,"got users api")
        return users
    }

    override suspend fun createNewChat(
        members: List<String>,
        chatName: String,
        chatId: String,
        profilePhoto: String,
        isGroup: Boolean
    ) {
//        val newMembers = members.toTypedArray()
        val newMembers:ArrayList<String> = ArrayList(members)
        Log.d(TAG,"Chat addition started api")

        val newChat = hashMapOf(
            "chatId" to chatId,
            "chatName" to chatName,
            "profilePhoto" to  profilePhoto,
            "isGroup" to isGroup,
            "members" to newMembers,
            "lastMessage" to listOf("NO message",Timestamp(0,0))
        )

        chatsCollection
            .document(chatId)
            .set(newChat)
            .addOnSuccessListener {
                Log.d(TAG,"added chat successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG,"Failed to add to chat : $e")
                throw e
            }
    }

    override suspend fun getChatsforMe(username: String): List<Chats> {
        val chats = mutableListOf<Chats>()
        Log.d(TAG,"Api the chats for usr : ${username}")
        val myChats = chatsCollection
            .whereArrayContains("members", username)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "Got the chats for usr : ${username}")

            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Unable to fetch the chats : $e")
            }

        for (doc in myChats.await().documents) {
            val chatId = doc.getString("chatId") ?: ""
            val chatName = doc.getString("chatName") ?: ""
            val isGroup = doc.getBoolean("isGroup") ?: false
            val members: List<Any> = doc.get("members") as List<Any>
            val lastMsg: List<Any> = doc.get("lastMessage") as List<Any>
            Log.d(TAG, "members are : $members")
            val singleChat = Chats(
                chatId = chatId,
                chatName = chatName,
                isGroup = isGroup,
                members = members.map { it.toString() },
                lastMessage = lastMessage(
                    content = lastMsg[0].toString(),
                    timestamp = lastMsg[1] as Timestamp
                )
            )
            chats.add(singleChat)
        }

        Log.d(TAG, "Chats in api are ${chats.size}")
        return chats
    }

    override suspend fun getUserChatData(username: String): chatUser {
        val userInfo = userCollection.whereEqualTo("username", username).get()

        try{
            val result = userInfo.await()
//            val username = username
            val fcmToken = result.documents[0].getString("fcmToken")?:""
            val profilePic = result.documents[0].getString("profilePic")?:""
            return chatUser(username,fcmToken,profilePic)
        }catch (e:Exception){
            Log.e(TAG,"Unable to get the user data : $e")
            return chatUser(profilePic = e.message.toString())
//            throw e
        }
    }


    override suspend fun sendNewMessage(message: MessageReceived) {
        val newMessage = hashMapOf(
            "content" to message.content,
            "chatId" to message.chatId,
            "senderId" to message.senderId,
            "timeStamp" to FieldValue.serverTimestamp()
        )

        chatsCollection.document(message.chatId,).collection("messages")
            .add(newMessage)
            .addOnSuccessListener {
                Log.d(TAG,"message added successfully")
            }
            .addOnFailureListener{e->
                Log.e(TAG,"failed to add to the database : $e")
            }
        val lastMessage = arrayListOf(
            message.content,
            message.timeStamp
        )
        chatsCollection.document(message.chatId)
            .update("lastMessage",lastMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Added lastMessage to : ${message.chatId}")
            }
            .addOnFailureListener { e->
                Log.e(TAG,"Unable to add the last Message :${message.chatId}  error : ${e}")
             }
//        chatsCollection.document(message.chatId).collection("messages")
    }

//    override suspend fun getTokenForMemebers(members: List<String>): List<String> {
//        val tokenList = mutableListOf<String>()
//
//        try{
//            for (member in members) {
//                val querySnap = userCollection
//                    .whereEqualTo("username", member)
//                    .get()
//                    .addOnSuccessListener {
//                        Log.d(TAG, "Successfully got token for $member ")
//                    }
//                    .addOnFailureListener { e ->
//                        Log.e(TAG, "Failed to get the token for $member")
//                    }
////            val currToken = querySnap.await().documents
//                for (doc in querySnap.await().documents) {
//                    val tokens:List<Any> = doc.get("fcmTokens") as List<Any>
//                    Log.d(TAG, "Token for $member is : $tokens")
//                    tokens.map {
//                        tokenList.add(it.toString())
//                    }
//                }
//            }
//        }catch(e:Exception){
//            Log.e(TAG,"Unable to send token to data : $e")
//        }

//        val queries = members.map { member ->
//            userCollection.whereEqualTo("username", member).get().addOnSuccessListener { querySnap ->
//                Log.d(TAG, "Successfully got token for $member")
//                val currToken:List<String> = querySnap.documents.mapNotNull { doc ->
//                    doc.getString("fcmToken")
//                }
//                tokenList.addAll(currToken)
//            }
//        }
//        queries.awaitAll()

//        return tokenList
//    }

    override suspend fun getMessagesForChat(currentChatId: String): List<MessageReceived> {
        val messages = mutableListOf<MessageReceived>()

        Log.d(TAG,"message getting started in api")
        val messageGet = chatsCollection.document(currentChatId).collection("messages")
            .get()
            .addOnSuccessListener {
                Log.d(TAG,"message got successfully")
            }
            .addOnFailureListener{e->
                Log.e(TAG,"Failed to get messages : $e")
                throw e
            }

        for( doc in messageGet.await()) {
            val content = doc.getString("content") ?: ""
            val chatId = doc.getString("chatId")?:""
            val senderId = doc.getString("senderId")?:""
            val timeStamp = doc.getTimestamp("timeStamp")?: Timestamp.now()

            val mess = MessageReceived(content, chatId, senderId,timeStamp)
            messages.add(mess)
        }

        return messages
    }

    override suspend fun deleteChat(chatId: String) {
        try {
            chatsCollection
                .document(chatId)
                .delete()
            Log.d(TAG,"Success to delete the chat : $chatId")
        }
        catch (e:Exception){
            Log.e(TAG,"Error in api to delete chatId ${chatId} : ${e}")
            throw e
        }
    }
}