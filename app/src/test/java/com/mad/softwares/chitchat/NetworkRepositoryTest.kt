package com.mad.softwares.chitchat

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.mad.softwares.chitchat.data.MessageReceived
import com.mad.softwares.chitchat.network.service
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertEquals
