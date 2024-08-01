package com.mad.softwares.chitchat.data

import com.google.firebase.Timestamp

data class Chats(
    val chatId:String = "",
    val chatName:String = "",
    val isGroup:Boolean = false,
    val members:List<String> = listOf(),
    val chatPic:String = "",
    val membersData:MutableList<chatUser> = mutableListOf(),
    val lastMessage:lastMessage = lastMessage(timestamp = Timestamp(0,0))
)
