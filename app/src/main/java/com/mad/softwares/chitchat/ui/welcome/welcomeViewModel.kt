package com.mad.softwares.chitchat.ui.welcome

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mad.softwares.chitchat.data.DataRepository
import com.mad.softwares.chitchat.data.User
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

val TAGview = "welcomeViewModel"

class welcomeViewModel(
    private val dataRepository: DataRepository,
    private val auth: FirebaseAuth,
): ViewModel() {
    var startUiState = MutableStateFlow(StartUiState())
        private set

    init {
        getFcmToken()
        checkForAuth()
    }

    fun getFcmToken(){
        if(startUiState.value.fcmToken!=""){
            return
        }
        Log.d(TAGview,"getFcmToken started")
        startUiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val token = dataRepository.getToken()
            try{ startUiState.update { it.copy(fcmToken = token ?: "", isLoading = false) } }
            catch (e:Exception){
                Log.d(TAGview,"getFcmToken failed : $e")
            }
        }
    }
    fun checkForAuth(){
        val currentUser = auth.currentUser
        if(currentUser != null){
            startUiState.update { it.copy(isUserInside = true) }
        }
        else{
            startUiState.update { it.copy(isUserInside = false) }
        }
    }

    fun updateUsername(username:String){
        startUiState.update{it.copy(username = username, emptyUsername = false)}

    }

    fun updatePassword(password:String){
        startUiState.update{it.copy(password = password, emptyPassword = false)}
    }

    private fun isEmptyUsername():Boolean{
        if(startUiState.value.username.isEmpty()){
            startUiState.update{it.copy(emptyUsername = true)}
            return true
        }
        else return false
    }

    private fun isEmptyPassword():Boolean{
        if(startUiState.value.password.isEmpty()){
            startUiState.update{it.copy(emptyPassword = true)}
            return true
        }
        else return false

    }

    private fun generateSixDigitUUID(n:Int): String {
        val randomUUID = UUID.randomUUID()
        val hashCode = Math.abs(randomUUID.hashCode()).toString()
        return hashCode.take(n).padStart(6, '0')
    }

    fun loginWithTheInfo(){
        Log.d(TAGview,"Login started Viewmodel")
        if (isEmptyPassword() || isEmptyUsername()) {
            return
        }
        startUiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {

            try {
//                getFcmToken()
                delay(1000)
                val newUser: Deferred<User> = async{
                    dataRepository.loginUser(
                        user = User(
                            fcmToken = startUiState.value.fcmToken,
                            uniqueId = generateSixDigitUUID(8),
                            username = startUiState.value.username,
                            password = startUiState.value.password,
                            docId = ""
                        ),
                        currFcmToken = startUiState.value.fcmToken
                    )

                }
                val user = newUser.await()
                if (user.username.isEmpty()) {
                    startUiState.update { it.copy(isError = true,
                        errorMessage = user.profilePic
                    ) }
                    throw Exception("User not found")
                }
                startUiState.update { it.copy(isUserInside = true,
                    ) }
            }
            catch (e:Exception){
                Log.d(TAGview,"Login failed error : $e")
                Log.d(TAGview,"signup failed error message: ${startUiState.value.errorMessage}")
                startUiState.update { it.copy(isError = true,) }
            }
            startUiState.update { it.copy(isLoading = false) }
        }
    }

    fun signUpWithTheInfo(){
        Log.d(TAGview,"signup started Viewmodel")
        if (isEmptyPassword() || isEmptyUsername()) {
            return
        }
        startUiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {

            delay(1000)
            try {
//                val signupUser:Deferred<User> = async {
//                    dataRepository.registerUser(
//                        user = User(
//                            fcmToken = startUiState.value.fcmToken,
//                            password = startUiState.value.password,
//                            username = startUiState.value.username,
//                            uniqueId = generateSixDigitUUID(8),
//                            docId = ""
//                        ),
//                        currFcmToken = startUiState.value.fcmToken
//                    )
//                }
//                val user = signupUser.await()
                val signupUser = dataRepository.registerUser(
                        user = User(
                            fcmToken = startUiState.value.fcmToken,
                            uniqueId = generateSixDigitUUID(8),
                            password = startUiState.value.password,
                            username = startUiState.value.username,
                            docId = ""
                        ),
                        currFcmToken = startUiState.value.fcmToken
                    )

                val user = signupUser
                if(user.username.isEmpty()){
                    startUiState.update { it.copy(isError = true,
                        errorMessage = user.profilePic
                    ) }
                    throw Exception("User not found")
                    }
                startUiState.update { it.copy(isUserInside = true) }
                Log.d(TAGview,"signup Success error message 1: ${startUiState.value.errorMessage}")
            }catch (e:Exception){
                Log.d(TAGview,"signup failed error : $e")
                Log.d(TAGview,"signup failed error message 2: ${startUiState.value.errorMessage}")
                startUiState.update { it.copy(isError = true) }
            }
            startUiState.update { it.copy(isLoading = false) }
        }

        Log.d(TAGview,"signup failed error message 3: ${startUiState.value.errorMessage}")
    }
}

data class StartUiState(
    val username:String ="",
    val password:String ="",

    val usernameExist:Boolean = false,
    val emptyUsername:Boolean= false,
    val emptyPassword:Boolean= false,
    val passIsSuccess:Boolean= false,

    val isLoading:Boolean = false,
    val isError:Boolean = false,
    val errorMessage:String = "",

    val isUserInside:Boolean = true,
    val fcmToken:String = "",
    val user: User = User()
//    val signUpSuccess:Boolean,
)
