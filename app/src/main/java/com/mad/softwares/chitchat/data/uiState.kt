package com.mad.softwares.chitchat.data
//
//import com.mad.softwares.chitchat.ui.ChatScreens
//import com.mad.softwares.chitchat.ui.isChatsLoading
//import com.mad.softwares.chitchat.ui.isPasswordReset
//import com.mad.softwares.chitchat.ui.isUsernamesLoading
//
//data class uiState(
////    val userId:String = "",
//    // for user fetchig
//    val fcmToken:String = "Loading....",
//    val userName:String = "",
//    val emptyUsername:Boolean = false,
//    val emptyPassword:Boolean = false,
//    val password:String = "",
//    val userNameExist:Boolean = false,
//
//    val uniqueAuth:String = "",
//    val profilePic:String = "https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg?w=740&t=st=1708193220~exp=1708193820~hmac=b17f7cebf9d68d37476c4db78b0631d9be333b664205ddaa5973dced193bf48e",
//    val groupPic:String = "",
//
//    //for getting errors and navigation
//    val passIsSuccess:Boolean = false,
//    val regsSuccess:Boolean = false,
//    val loginSuccess:Boolean = false,
//    val passFound:Boolean = true,
//    val isPasswordResetSuccess:isPasswordReset = isPasswordReset.maybe,
//    val overAllError:Boolean = false,
//
//    //for authentication
//    val isAuthenticated:Boolean = false,
//    val navAuthenticate:Boolean = false,
//    val wrongOtp:Boolean = false,
//    val isLoading:Boolean = false,
//    val isRefreshing:Boolean = false,
//
//    // for myuser data
//    val myUserData:User = User(),
//    val myTempUserData:User = User(),
//
//    //for chats purpose
//    val allAvailableUsers:List<String> = listOf(),
//    val allAvailableChats:List<Chats> = listOf(),
//    val isChatsButtonEnabled: ChatScreens = ChatScreens.FiringUp,
//    val isMyChatsLoading:isChatsLoading = isChatsLoading.Loading,
//
//    //All Users
//    val isUsersLoading:isUsernamesLoading = isUsernamesLoading.Loading,
//    val members:List<String> = listOf(),
//
//    //Messages
//    val messagesForChat:List<MessageReceived> = listOf(),
//    val currentChat:Chats = Chats(),
//    val listOfMembersForToken:List<String> = listOf(),
//    val messageToSend:String = ""
//)
