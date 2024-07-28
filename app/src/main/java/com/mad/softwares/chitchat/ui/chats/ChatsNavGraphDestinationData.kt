package com.mad.softwares.chitchat.ui.chats
//Ch
//import android.util.Log
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.navigation.NavHost
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.mad.softwares.chitchat.R
//import com.mad.softwares.chitchat.ui.GodViewModelProvider
//import com.mad.softwares.chitchat.ui.TAGnav
//import com.mad.softwares.chitchat.ui.destinationData
//import com.mad.softwares.chitchat.ui.welcome.welcomeDestination
//
//object ChatsNavGraphDestinationData : destinationData {
//    override val route="chatsNavGraph"
//    override val title= R.string.chats
//    override val canBack= false
//
//}
//
//@Composable
//fun ChatsNavGraph(
//    navController: NavHostController,
//    modifier:Modifier = Modifier
////    startdestinationData: destinationData
//) {
//    val chatsViewModel:ChatsViewModel = viewModel(factory = GodViewModelProvider.Factory)
//    NavHost(navController = navController,
//        startDestination = ChatsNavGraphDestinationData.route,
//        modifier = modifier){
//        composable(chatsScreenDestination.route) {
////            Log.d(TAGnav,"chats : ${addChatDestination.routeWithArgs()}")
//            UserChats(
//
//                navitageToAddChats = {
//                    Log.d(TAGnav,"chats : ${addChatDestination.route}")
//                    navController.navigate(addChatDestination.route) },
//                navigateToWelcome = {
//                    navController.navigate(welcomeDestination.route) {
//                        popUpTo(0)
//                    }
//                },
//                viewModel = chatsViewModel
//            )
//        }
//
//        composable(
//            route = addChatDestination.route
//        )
//        {
//            AddChat(
//                navigateUp = {navController.popBackStack(chatsScreenDestination.route,false)},
//                viewModel = chatsViewModel
//            )
//        }
//    }
//}