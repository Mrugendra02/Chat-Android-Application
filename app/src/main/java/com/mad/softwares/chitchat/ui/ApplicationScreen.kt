package com.mad.softwares.chitchat.ui

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.mad.softwares.chitchat.R
import com.mad.softwares.chitchat.data.Chats
import com.mad.softwares.chitchat.data.User
import com.mad.softwares.chitchat.data.uiState
import com.mad.softwares.chitchat.ui.theme.ChitChatTheme


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ApplicationScreen(
    appViewModel: chitChatViewModel,
    appUiState: uiState
) {
    StartScreen(
        appUiState = appUiState,
        updateUsername = { appViewModel.updateUsername(it) },
        updatePassword = { appViewModel.updatePassword(it) },
        registerUser = { appViewModel.registerUser() },
        checkUserExist = { appViewModel.userExistCheck() },
        registrationSuccess = { appViewModel.registrationAuthenticated() },
        updateForLogin = { appViewModel.updateForLogin() },
        loginUser = { appViewModel.loginUser() },
        loginUsername = { appViewModel.loginUsername(it) },
        loginSuccess = { appViewModel.loginAuthenticated() },
        authText = { appViewModel.handelAuthTextField(it) },
        authenticationById = { appViewModel.authenticateUserByUserId() },
        authenSuccess = { appViewModel.authenSuccess() },
        logOut = { appViewModel.logoutUser() },
        updateForSignUp = {appViewModel.updateForSignUp()},
        resetPassword = {appViewModel.resetPassword()},
        passResetSuccess = {appViewModel.passResetSuccess()},
        getAllUsers = {appViewModel.updateAllTheUsers()},
        getAllUsersForced = {appViewModel.updateAllTheUsers(true)},
        addChatOrGroup = {appViewModel.createNewChat(it)},
        currentChat = {appViewModel.setCurrentChatId(it)},
        reloadChats = { appViewModel.getAllAvailableChats(it) },
        sendMessageTo = {appViewModel.sendMessageTo()},
        updateMessage = {appViewModel.updateMessageToSend(it)},
        sendNotification = { appViewModel.sendNotification(it)},
        getNewMessages = { appViewModel.getMessages()},
        chatDelete = {appViewModel.deleteChatUi(it)},
        permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS),
    )
}

enum class ChatScreens(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Register(title = R.string.register_with_us),
    Login(title = R.string.login_now),
    Authen(title = R.string.authetication),
    OTPAuthen(title = R.string.otp_athentication),
    AllChats(title = R.string.chats),
    FiringUp(title = R.string.starting_chitchat),
    ResetPassword(title = R.string.reset_password),
    SelectFriend(title = R.string.select_friend),
    MyMessages(title = R.string.my_messages_default)
}

//val TAG = "ui_LOG"

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun StartScreen(
    appUiState: uiState,
    modifier: Modifier = Modifier,
    updateUsername: (String) -> Unit,
    updatePassword: (String) -> Unit,
    registerUser: () -> Unit,
    checkUserExist: () -> Unit,
    registrationSuccess: () -> Unit,
    loginSuccess: () -> Unit,
    updateForLogin: () -> Unit,
    loginUser: () -> Unit,
    loginUsername: (String) -> Unit,
    authText: (String) -> Unit,
    authenticationById: () -> Unit,
    authenSuccess: () -> Unit,
    logOut: () -> Unit,
    updateForSignUp:()->Unit,
    resetPassword:()->Unit,
    passResetSuccess:()->Unit,
    getAllUsers:()->Unit,
    getAllUsersForced: () -> Unit,
    addChatOrGroup:(String)->Unit,
    currentChat:(Chats)->Unit,
    reloadChats:(Boolean)->Unit,
    sendMessageTo : ()->Unit,
    updateMessage :(String)->Unit,
    sendNotification:(String)->Unit,
    getNewMessages:()->Unit,
    chatDelete:(String)->Unit,
    permissionState: PermissionState,
) {
    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = ChatScreens.valueOf(
        backStackEntry?.destination?.route ?: ChatScreens.Start.name
    )


//    val startDestination: ChatScreens = when (appUiState.isChatsButtonEnabled) {
//        isChatButton.Yes -> {
//            ChatScreens.AllChats
//        }
//
//        isChatButton.No -> {
//            ChatScreens.Start
//        }
//
//        isChatButton.Loading -> {
//            ChatScreens.FiringUp
//        }
//    }

    Scaffold(
        topBar = {
            AppTopbar(
                currentScreen = currentScreen,
                canGoBack = navController.previousBackStackEntry != null,
                goBack = { navController.navigateUp() },
                navController = navController,
                appUiState = appUiState,
                logOut = logOut,
                getMessages = getNewMessages
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = appUiState.isChatsButtonEnabled.name,
            modifier = modifier.padding(it),
        ) {
            composable(
                route = ChatScreens.Start.name
            ) {
                StartHere(
                    appUiState = appUiState,
                    registerButton = {
                        updateForSignUp()
                        navController.navigate(ChatScreens.Register.name)
                    },
                    chatsButton = {
                        navController.navigate(ChatScreens.AllChats.name)
                    },
                    loginButton = {
                        updateForLogin()
                        navController.navigate(ChatScreens.Login.name)
                    },
                )
            }

            composable(ChatScreens.Register.name) {
                Registration(
                    buttonFunction = {
                        registerUser()
                    },
                    appUiState = appUiState,
                    updateUsername = updateUsername,
                    updatePassword = updatePassword,
                    buttonText = R.string.register_now,
                    forgotPassword = {
//                        Navigation to recover password
                    },
                    showForgotButton = false,
                    successButton = {
                        navController.navigate(ChatScreens.Authen.name)
                    }
                )
            }
            composable(ChatScreens.Login.name) {
                Registration(
                    buttonFunction = {
                        loginUser()
                    },
                    appUiState = appUiState,
                    updateUsername = loginUsername,
                    updatePassword = updatePassword,
                    buttonText = R.string.login_now,
                    forgotPassword = {
//                        Navigation to recover password
                        navController.navigate(ChatScreens.ResetPassword.name)

                    },
                    showForgotButton = true,
                    successButton = {
                        navController.navigate(ChatScreens.OTPAuthen.name)
                    }
                )
            }
            composable(ChatScreens.Authen.name) {
                Authentication(
                    appUiState = appUiState,
                    goToChats = {
                        navController.navigate(ChatScreens.AllChats.name)

                    },
                    backHandler = {
//                        navController.popBackStack(
//                            ChatScreens.Start.name,
//                            inclusive = false
//                        )
                    }
                )
            }
            if (appUiState.regsSuccess) {
                registrationSuccess()
                navController.navigate(ChatScreens.Authen.name)
            }
            if (appUiState.loginSuccess) {
                loginSuccess()
                navController.navigate(ChatScreens.OTPAuthen.name)
            }
            if (appUiState.navAuthenticate) {
                authenSuccess()
                navController.popBackStack(ChatScreens.Start.name,inclusive = true)
            }
            if(appUiState.isPasswordResetSuccess == isPasswordReset.Yes){
                passResetSuccess()
//                navController.navigate(ChatScreens.AllChats.name){
//                    popUpTo(ChatScreens.AllChats.name){
//                        inclusive = true
//                    }
//                }
                navController.navigateUp()
            }

            composable(ChatScreens.OTPAuthen.name) {
//                Log.d(TAG,"Navigate to otp auth")
                OTPAuten(
                    appUiState = appUiState,
                    goToChats = {
                        authenticationById()
                    },
                    authText = authText,
                    backHandler = {
                        navController.popBackStack(
                            ChatScreens.Start.name,
                            inclusive = false
                        )
                    }
                )
            }

            composable(ChatScreens.AllChats.name) {
                reloadChats(false)

                AllChats(
                    backHandler =
                    {
//                        this.finishAffinity()
                    },
                    appUiState = appUiState,
                    user = appUiState.myUserData,
                    addChat = {
                        getAllUsers()
                        navController.navigate(ChatScreens.SelectFriend.name)
                    },
                    currentChat = {
                        currentChat(it)
                                  navController.navigate(ChatScreens.MyMessages.name)},
                    refreshState = rememberPullRefreshState(
                        refreshing = appUiState.isRefreshing,
                        onRefresh = { reloadChats(true) }),
                    reloadChats = { reloadChats(false) },
                    chatDelete = {chatDelete(it)},
                    permissionState = permissionState
                )
            }

            composable(ChatScreens.FiringUp.name) {
                FiringUp(appUiState = appUiState)
            }

            composable(ChatScreens.ResetPassword.name){
                ResetPassword(
                    appUiState = appUiState,
                    updatePassword = updatePassword,
                    updateUsername = updateUsername,
                    updateUniqueId = authText,
                    backHandler = {
                        navController.popBackStack(ChatScreens.Start.name,inclusive = false)
                    },
                    goToHome = {
                        resetPassword()
//                        navController.popBackStack(ChatScreens.Start.name,inclusive = false)
                    })
            }

            composable(ChatScreens.SelectFriend.name){
                AllUsers(
                    appUiState = appUiState,
                    setCurrentUser =
                    {
                        addChatOrGroup(it)
                        reloadChats(true)
                        navController.navigateUp()
                    },
                    refreshState = rememberPullRefreshState(
                        refreshing = appUiState.isRefreshing,
                        onRefresh = {getAllUsersForced()}))
            }

            composable(ChatScreens.MyMessages.name) {
                MyMessages(
                    appUistate = appUiState,
                    sendMessage = sendMessageTo,
                    updateMessage = updateMessage,
                    getNewMessages = getNewMessages)
//
//                ResetPassword(
//                    appUiState = appUiState,
//                    updatePassword = {},
//                    updateUsername = {},
//                    updateUniqueId = {},
//                    backHandler = { /*TODO*/ }) {
//
//                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopbar(
    currentScreen: ChatScreens,
    canGoBack: Boolean,
    goBack: () -> Unit,
    navController: NavHostController,
    logOut: () -> Unit,
    appUiState: uiState,
    getMessages:()->Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    TopAppBar(
        title = {
            Text(
                text = stringResource(currentScreen.title),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            if (currentScreen == ChatScreens.Authen || currentScreen == ChatScreens.OTPAuthen ) {
                IconButton(onClick = {
                    navController.popBackStack(
                        ChatScreens.Start.name,
                        inclusive = false
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = ""
                    )
                }
            }
                else if(currentScreen == ChatScreens.Start){
                    Icon(imageVector = Icons.Filled.Home,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "")
                }
                else if (currentScreen != ChatScreens.AllChats) {
                    if (canGoBack) {
                        IconButton(onClick = goBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = ""
                            )
                        }
                    }
                }
        },
        actions = {
            if (currentScreen == ChatScreens.AllChats) {
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = ""
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                )
                {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.logout)) },
                        onClick = {
//                            Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
                            logOut()
                            if(canGoBack){
                                navController.popBackStack(
                                    ChatScreens.Start.name,
                                    inclusive = false
                                )
                            }
                            else{
                                navController.navigate(ChatScreens.Start.name)
                            }
                        })
                    DropdownMenuItem(
                        text = { Text(text = appUiState.myUserData.username) }, 
                        onClick = { /*TODO*/ })
                }
            }

            if (currentScreen == ChatScreens.MyMessages) {

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = ""
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                )
                {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.profile_info)) },
                        onClick = {
//                            Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()

                        })
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.reload_chat)) },
                        onClick = {
                            getMessages()
                            expanded = false
                        })
                }
            }
        }

    )
}

//@Preview
//@Composable
//fun PreviewStartScreen() {
//    ChitChatTheme {
//        StartScreen(
//            appUiState = uiState(),
//            updatePassword = {},
//            updateUsername = {},
//            registerUser = {},
//            checkUserExist = {},
//            registrationSuccess = {},
//            updateForLogin = {},
//            loginUser = {},
//            loginUsername = {},
//            loginSuccess = {},
//            authText = {},
//            authenticationById = {},
//            authenSuccess = {}
//        )
//    }
//}

@Preview
@Composable
fun PreviewTopBar() {
    ChitChatTheme(
        dynamicColor = false
    ) {
        AppTopbar(
            currentScreen = ChatScreens.AllChats,
            canGoBack = true,
            goBack = { /*TODO*/ },
            navController = NavHostController(context = LocalContext.current),
            appUiState = uiState(
            ),
            logOut = {},
            getMessages = {}
        )
    }
}




