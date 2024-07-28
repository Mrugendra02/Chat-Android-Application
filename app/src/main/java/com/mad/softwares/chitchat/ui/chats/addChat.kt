package com.mad.softwares.chitchat.ui.chats

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mad.softwares.chitchat.R
import com.mad.softwares.chitchat.data.chatUser
import com.mad.softwares.chitchat.ui.ApptopBar
import com.mad.softwares.chitchat.ui.GodViewModelProvider
import com.mad.softwares.chitchat.ui.LoadingIndicator
import com.mad.softwares.chitchat.ui.destinationData

object addChatDestination : destinationData {
    override val route = "AddChat"
    override val title = R.string.add_chats
    override val canBack = true
//    val members = listOf<String>("")
//    val memberString = members?.joinToString(",") ?: ""
//
//    //    val routeWithArgs = "$route/${memberString}"
    fun routeWithArgs(members: List<String>): String {
        val membersString = members.joinToString(",")
        return "$route/$membersString"
    }
}

@Composable
fun AddChat(
    navigateUp: () -> Unit,
    viewModel: AddChatViewModel = viewModel(factory = GodViewModelProvider.Factory),
    navigateWithReload: (Boolean) -> Unit,
//    viewModel: ChatsViewModel
//    jugadViewModel:chatsViewModel = viewModel(factory = GodViewModelProvider.Factory)
) {
    val uiState = viewModel.addChatUiState.collectAsState().value
    if (uiState.addChatSuccess){
//        jugadViewModel.getChats(true)
//        viewModel.getChats(isForced = true)
//        navigateUp()
        navigateWithReload(true)
    }
    AddChatBody(
        navigateUp = navigateUp,
        addChatUiState = uiState,
        addSingleMember = {viewModel.addSingleChat(it)}
    )
    if(uiState.isLoading){
        LoadingIndicator(isLoading = uiState.isLoading)
    }
    else if(uiState.isError){
        ShowErrorAndRetry {
            viewModel.getMembers()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChatBody(
    navigateUp: () -> Unit,
    addChatUiState: AddChatUiState,
    addSingleMember: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = { ApptopBar(destinationData = addChatDestination, scrollBehavior = scrollBehavior,
            navigateUp = { navigateUp() }) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            items(addChatUiState.chatUsers){
            SingleChatPerson(chatUser = it, addSingleMember = addSingleMember, isCardEnabled =true)
        }
        }
    }
}

@Composable
fun SingleChatPerson(
    chatUser:chatUser,
    addSingleMember:(String)->Unit,
    isCardEnabled:Boolean,
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(5.dp)
        ,
        onClick = { addSingleMember(chatUser.username)
        },
        enabled = isCardEnabled
    ) {
        Row(
            modifier = modifier
                .fillMaxHeight()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                modifier = modifier
                    .size(40.dp),
                imageVector = Icons.Default.Person,
                contentDescription = ""
            )
            Text(
                modifier =modifier
                    .fillMaxWidth()
                ,
                text = chatUser.username,
                fontSize = 25.sp,
                fontWeight = FontWeight(700),
                textAlign = TextAlign.Center
            )
        }

    }
}

@Preview
@Composable
fun AddChatPreview() {
    AddChatBody(addChatUiState = AddChatUiState(
        chatUsers = listOf(
            chatUser(username = "Hello@123.com"),
            chatUser(username = "There@456.com"),
            chatUser(username = "getThis@790.com"),
            chatUser(username = "Hello@123.com"),
            chatUser(username = "There@456.com"),
            chatUser(username = "getThis@790.com"),
            chatUser(username = "Hello@123.com"),
            chatUser(username = "There@456.com"),
            chatUser(username = "getThis@790.com"),
            chatUser(username = "Hello@123.com"),
            chatUser(username = "There@456.com"),
            chatUser(username = "getThis@790.com"),
            chatUser(username = "Hello@123.com"),
            chatUser(username = "There@456.com"),
            chatUser(username = "getThis@790.com"),
            chatUser(username = "Hello@123.com"),
            chatUser(username = "There@456.com"),
            chatUser(username = "getThis@790.com"),
            )
    ),
        navigateUp = {},
        addSingleMember = {})
}




