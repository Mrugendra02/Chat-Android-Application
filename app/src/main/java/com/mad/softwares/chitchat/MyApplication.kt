package com.mad.softwares.chitchat

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestoreSettings

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.mad.softwares.chitchat.data.AppContainer
import com.mad.softwares.chitchat.data.DefaultAppContainer

class MyApplication:Application() {
    lateinit var container:AppContainer
    private lateinit var auth:FirebaseAuth

    override fun onCreate() {
        super.onCreate()

        try{
            FirebaseApp.initializeApp(this)
            Log.d("FirebaseLogs","initialized success")
        }
        catch (e:Exception){
            Log.d("FirebaseLogs","error in initializing")
        }

        if(BuildConfig.DEBUG){
            val firestore = Firebase.firestore
            firestore.useEmulator("10.0.2.2", 8080)

            firestore.firestoreSettings = firestoreSettings {
                isPersistenceEnabled = false
            }

            Firebase.auth.useEmulator("10.0.2.2", 9099)
        }


        container = DefaultAppContainer()
    }
}