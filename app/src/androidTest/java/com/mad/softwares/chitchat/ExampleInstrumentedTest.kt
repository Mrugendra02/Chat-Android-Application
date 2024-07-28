package com.mad.softwares.chitchat

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mad.softwares.chitchat.network.Notification
import com.mad.softwares.chitchat.network.NotificationRequest
import com.mad.softwares.chitchat.network.service
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.mad.softwares.chitchat", appContext.packageName)
    }


    @Test
    @Throws(Exception::class)
    fun fcmApiService_postNotification_returnSuccess() {
        runBlocking {

            val notificationRequest = NotificationRequest(
                to = "fDAAkD_bSyahTJa1eLduJJ:APA91bExxniUk09djfV95r7JCHntX_bOmgPlFRTXGJx8cqq558y56gUtixJPVbH3Hp2Z4GWGRV_DrAHMwRk6-C1g-SRm3y19SofOytczVAkNnG4NeqkjI81jlpErZnRd_Z7Xmfbqrq91",
                notification = Notification("Test1", "Notificatin test ing")
            )
            val respond = service.sendNotification(notificationRequest = notificationRequest)


            assertEquals(200, respond.code())
            Log.d("Test_notfi_api", respond.body().toString())
//        val respondBody = respond.body().toString()
//        val jsonObj = respondBody.let { JSONObject(it) }
//        val responseCode = jsonObj.getInt("success")
//        assertEquals(1,responseCode)
        }
    }
}