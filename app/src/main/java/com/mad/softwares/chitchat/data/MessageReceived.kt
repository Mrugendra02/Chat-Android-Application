package com.mad.softwares.chitchat.data

import com.google.firebase.Timestamp

data class MessageReceived(
    val content:String = "",
    val chatId:String = "",
    val senderId:String = "",
    val timeStamp:Timestamp = Timestamp.now()
)
