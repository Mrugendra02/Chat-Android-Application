package com.mad.softwares.chitchat.data

import com.google.firebase.firestore.firestore
import com.mad.softwares.chitchat.network.FirebaseApi
import com.mad.softwares.chitchat.network.NetworkFirebaseApi

interface AppContainer {
    val dataRepository:DataRepository
    val apiService:FirebaseApi

}

class DefaultAppContainer : AppContainer{
    private val db = com.google.firebase.Firebase.firestore
    private val userCollection = db.collection("Users")
    private val chatsCollection = db.collection("Chats")
    private val messagesCollection = db.collection("Messages")


    override val apiService: FirebaseApi = NetworkFirebaseApi(db,userCollection,chatsCollection,messagesCollection,)
    override val dataRepository: DataRepository = NetworkDataRepository(apiService)
}