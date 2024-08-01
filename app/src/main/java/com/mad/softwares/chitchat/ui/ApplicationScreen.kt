package com.mad.softwares.chitchat.ui

import android.util.Log
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mad.softwares.chitchat.R
import com.mad.softwares.chitchat.ui.chats.AddChat
import com.mad.softwares.chitchat.ui.chats.UserChats
import com.mad.softwares.chitchat.ui.chats.addChatDestination
import com.mad.softwares.chitchat.ui.chats.chatsScreenDestination
import com.mad.softwares.chitchat.ui.messages.Messages
import com.mad.softwares.chitchat.ui.theme.ChitChatTheme
import com.mad.softwares.chitchat.ui.welcome.LoginScreen
import com.mad.softwares.chitchat.ui.welcome.SignUpScreen
import com.mad.softwares.chitchat.ui.welcome.loginScreenDestination
import com.mad.softwares.chitchat.ui.welcome.signupScreenDestination
import com.mad.softwares.chitchat.ui.welcome.welcomeDestination
import com.mad.softwares.chitchat.ui.welcome.WelcomeScreen
import com.mad.softwares.chitchat.ui.messages.messagesdestinationData

val TAGnav = "navLog"

@Composable
fun ApplicationScreen(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(loginScreenDestination.route) {
            LoginScreen(
                navigateUp = {
                    navController.popBackStack(
                        welcomeDestination.route,
                        inclusive = false
                    )
                },
                navigateToChats = {
//                    navController.popBackStack()
//                    navController.navigate(chatsScreenDestination.route) {
//                        popUpTo(0)
//                    }
                    navController.navigate("${chatsScreenDestination.route}/${false}"){
                        popUpTo(0)
                    }
                }
            )
        }
        composable(signupScreenDestination.route) {
            SignUpScreen(
                navigateUp = { navController.popBackStack(welcomeDestination.route, false) },
                navigateToChats = {
//                    navController.popBackStack()
//                    navController.navigate(chatsScreenDestination.route) {
//                        popUpTo(0)
//                    }
                    navController.navigate("${chatsScreenDestination.route}/${false}"){
                        popUpTo(0)
                    }
                }
            )
        }
        composable(welcomeDestination.route) {
            WelcomeScreen(
                navigateToLogin = {
                    navController.navigate(loginScreenDestination.route)
                },
                navigateToSignUp = {
                    navController.navigate(signupScreenDestination.route)
                }
            )
        }

        composable(
            route = chatsScreenDestination.routeWithReload,
            arguments = listOf(navArgument(chatsScreenDestination.toReloadChats){
                type = NavType.BoolType
                defaultValue = false
            })
        ) {
//            Log.d(TAGnav,"chats : ${addChatDestination.routeWithArgs()}")
            UserChats(

                navitageToAddChats = {
                    Log.d(TAGnav,"chats : ${addChatDestination.routeWithArgs(it)}")
                    navController.navigate(addChatDestination.routeWithArgs(it)) },
                navigateToWelcome = {
                    navController.navigate(welcomeDestination.route) {
                        popUpTo(0)
                    }
                },
                navigateToCurrentChat = {
                    navController.navigate("${messagesdestinationData.route}/${it}")
                }
            )
        }

        composable(
            route = "${addChatDestination.route}/{members}",
            arguments = listOf(
                navArgument("members") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        )
        {
            AddChat(
                navigateUp = {navController.popBackStack(chatsScreenDestination.routeWithReload,false)},
                navigateWithReload = {
                    navController.navigate("${chatsScreenDestination.route}/$it"){
                        popUpTo(0)
                    }
                }
            )
        }

//        composable(ChatsNavGraphDestinationData.route){
//            ChatsNavGraph(navController)
//        }

        composable(
            route = messagesdestinationData.routeWithArgs,
            arguments = listOf(
                navArgument(messagesdestinationData.chatIDAndUsername){
                    type= NavType.StringType
                    defaultValue = ""
                }
            )
        ){
            Messages(
                navigateUp = {navController.navigateUp()}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApptopBar(
    destinationData: destinationData,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title:String ="",
    navigateUp: () -> Unit,
    action: @Composable (RowScope.() -> Unit) ={},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            if(title==""){
                Text(
                    text = stringResource(id = destinationData.title),
//                color = MaterialTheme.colorScheme.onPrimary
                )
            }else{
                Text(text = title)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary
//            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        navigationIcon =
        {
            if (destinationData.canBack) {

                IconButton(onClick = { navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back",
//                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

            }
        },
        actions = action,
        scrollBehavior = scrollBehavior,

        )
}

object welcomeDestinationTest : destinationData {
    override val route = "welcome"
    override val title = R.string.welcome
    override val canBack = false
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ApptopBarPreview() {
    ChitChatTheme(dynamicColor = false) {
        ApptopBar(
            welcomeDestinationTest,
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
            navigateUp = {},
            title = "Testing this"
        )
    }
}

interface destinationData {
    val route: String
    val title: Int
    val canBack: Boolean
}
