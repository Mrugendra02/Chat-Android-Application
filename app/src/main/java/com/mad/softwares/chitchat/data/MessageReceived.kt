package com.mad.softwares.chitchat.data

import com.google.firebase.Timestamp

enum class ContentType(){
    text,
    image,
    document,
    audio,
    video
}

enum class messageStatus(){
    Sending,
    Send,
    Delivered,
    Read,
    Error
}

data class MessageReceived(
    val content:String = "",
    val contentType:ContentType = ContentType.text,
    val senderId:String = "",
    val timeStamp:Timestamp = Timestamp.now(),
    val status:messageStatus = messageStatus.Send,
)
