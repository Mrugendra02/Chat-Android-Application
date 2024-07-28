package com.mad.softwares.chitchat.network

import com.squareup.moshi.Moshi
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


//@JsonClass(generateAdapter = true)
data class NotificationRequest(
//    @Json(name = "to")
    val to: String,
//    @Json(name = "notification")
    val notification: Notification
)

//@JsonClass(generateAdapter = true)
data class Notification(
//    @Json(name = "body")
    val body: String,
//    @Json(name = "title")
    val title: String
)

data class NotificationResponse(
    val multicast_id: Long,
    val success: Int,
    val failure: Int,
    val canonical_ids: Int,
    val results: List<Result>
)

data class Result(
    val message_id: String
)

val moshi = Moshi.Builder().build()

//val retrofit = Retrofit.Builder()
//    .baseUrl("https://fcm.googleapis.com/")
//    .addConverterFactory(MoshiConverterFactory.create(moshi))
//    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl("https://fcm.googleapis.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val service = retrofit.create(FcmApiService::class.java)