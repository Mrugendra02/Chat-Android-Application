package com.mad.softwares.chitchat.data

import com.google.firebase.Timestamp

data class lastMessage(
    val content:String = "",
    val timestamp: Timestamp
)
