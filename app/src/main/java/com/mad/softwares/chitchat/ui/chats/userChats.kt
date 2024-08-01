package com.mad.softwares.chitchat.ui.chats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mad.softwares.chitchat.R
import com.mad.softwares.chitchat.data.Chats
import com.mad.softwares.chitchat.data.User
import com.mad.softwares.chitchat.ui.ApptopBar
import com.mad.softwares.chitchat.ui.GodViewModelProvider
import com.mad.softwares.chitchat.ui.destinationData
import com.mad.softwares.chitchat.ui.theme.ChitChatTheme

object chatsScreenDestination : destinationData {
    override val route = "chats"
    override val title = R.string.chats
    override val canBack = false
    val toReloadChats = "reloadStatus"
    val routeWithReload = "$route/{$toReloadChats}"
}

@Composable
fun UserChats(
    viewModel: ChatsViewModel = viewModel(factory = GodViewModelProvider.Factory),
//    viewModel: ChatsViewModel,
    navitageToAddChats: (List<String>) -> Unit,
    navigateToWelcome:()->Unit,
    navigateToCurrentChat: (String) -> Unit
) {


    val chatsUiState = viewModel.chatsUiState.collectAsState().value
    when (chatsUiState.currentChatStatus) {
        CurrentChatStatus.Success -> {
            UserChatsBody(
                chatsUiState = chatsUiState,
                navigateToCurrentChat = navigateToCurrentChat,
                isCardEnabled = true,
                navigateToAddChats = { navitageToAddChats(viewModel.getMembers()) },
                logOut = { viewModel.logoutUser() }
            )
        }

        CurrentChatStatus.Loading -> {
            ShowChatsLoading()
        }

        CurrentChatStatus.Failed -> {
            ShowErrorAndRetry {
                viewModel.getChats(isForced = true)
            }
        }

        CurrentChatStatus.Logouted -> {
            navigateToWelcome()
        }
    }
}



@Preview
@Composable
fun ShowErrorChatsPreview(){
    ShowErrorAndRetry(retry = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChatsBody(
    chatsUiState: ChatsUiState,
    navigateToCurrentChat: (String) -> Unit,
    isCardEnabled: Boolean,
    navigateToAddChats: () -> Unit,
    logOut:()->Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()
    val expandedFab by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }
    var expandDDMenu by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            AddChatFab(
                expanded = expandedFab,
                navigateToAddChat = { navigateToAddChats() })
        },
        topBar = {
            ApptopBar(
                destinationData = chatsScreenDestination,
                scrollBehavior = scrollBehavior,
                navigateUp = { /*TODO*/ },
                action = {
                    IconButton(onClick = { expandDDMenu = !expandDDMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More"
                        )
                    }
                    DropdownMenu(
                         expanded = expandDDMenu,
                        onDismissRequest = { expandDDMenu = !expandDDMenu }) {
                            DropdownMenuItem(
                                text = { Text(text = "Logout from here") },
                                onClick = logOut
                            )
                        
                    }
                }
            )
        },

        ) { paddingValues ->
        ShowChatsSuccessful(
            chatsUiState = chatsUiState,
            paddingValues = paddingValues,
            listState = listState,
            navigateToCurrentChat = navigateToCurrentChat,
            isCardEnabled = isCardEnabled
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowChatsLoading(

) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ApptopBar(
                destinationData = chatsScreenDestination,
                scrollBehavior = scrollBehavior,
                navigateUp = { /*TODO*/ }
            )
        },
//        floatingActionButton = {
//            FloatingActionButton(onClick = { addChat()}) {
//                Icon(
//                    imageVector = Icons.Rounded.Add,
//                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
//                    contentDescription = "")
//            }
//        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
//                .pullRefresh(state = refreshState)
        ) {
//            Column{
//                Text(text = "Here all The chats wiil be displayed")
//                Text(text = "Current userId is : ${appUiState.myUserData.username}")
//            }
            LazyColumn(
                modifier = Modifier,
//                    .padding(bottom = 100.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(8) { chat ->
                    ChatLoading()
                }
            }
//            PullRefreshIndicator(
//                refreshing = appUiState.isRefreshing,
//                state = refreshState,
//                modifier = Modifier.align(Alignment.TopCenter),
//                backgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
//                contentColor = MaterialTheme.colorScheme.onBackground
//            )

        }
    }
}

@Composable
fun ChatLoading() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(5.dp)
//            .clickable(onClick = { currentChat(chat.chatId) }),
//        border = BorderStroke(5.dp,MaterialTheme.colorScheme.primary)
//        onClick = currentChat(chat.chatId)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary
                )
            )
            {}
            Spacer(Modifier.height(15.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {}
        }
    }
}

@Preview
@Composable
fun ChatLoadingPreview() {
    ShowChatsLoading()
}

@Composable
fun ShowChatsSuccessful(
    chatsUiState: ChatsUiState,
    paddingValues: PaddingValues,
    listState: LazyListState,
    navigateToCurrentChat: (String) -> Unit,
    isCardEnabled: Boolean
) {
    if (chatsUiState.chats.isEmpty()) {
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No chats yet press below icon to start new chats",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(chatsUiState.chats) {
                SingleChat(
                    chat = it,
                    navigateToCurrentChat = navigateToCurrentChat,
                    isCardEnabled = isCardEnabled
                )
            }
        }
    }
}

@Composable
fun SingleChat(
    chat: Chats,
    navigateToCurrentChat: (String) -> Unit,
    isCardEnabled: Boolean
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(5.dp),
        onClick = {
            navigateToCurrentChat("${chat.chatId},${chat.chatName}")

        },
        enabled = isCardEnabled
        //        border = BorderStroke(5.dp,MaterialTheme.colorScheme.primary)

    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Text(
                    text = chat.chatName,
                    fontSize = 25.sp
                )
                Spacer(Modifier.height(15.dp))
//                Text(
//                    text = if(chat.lastMessage.timestamp == Timestamp(0,0)){
//                        "Not yet contacted"
//                    } else {
//                        "${newDate}"
//                    },
//                    fontSize = 25.sp
//                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
//                .fillMaxWidth()
                    .wrapContentSize(Alignment.BottomEnd)
            ) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Delete Chat"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                )
                {
//                    DropdownMenuItem(
//                        text = { Text(text = "Delete chat") },
//                        onClick = { chatDelete() })
                }
            }
        }
    }
}


@Composable
fun AddChatFab(
    expanded: Boolean = false,
    navigateToAddChat: () -> Unit,
) {
    ExtendedFloatingActionButton(
        onClick = { navigateToAddChat() },
        icon = {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.add_chats)
            )
        },
        text = { Text(text = "New Chat") },
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        expanded = expanded
    )
}

@Composable
@Preview(showSystemUi = false, showBackground = false)
fun UserChatsPreview() {
    ChitChatTheme {
        UserChatsBody(
            chatsUiState = ChatsUiState(
                currentUser = User(username = "mrg@123.com"),
                chats = listOf(
                    Chats(
                        chatName = "mrg@123.com"
                    ),
                    Chats(
                        chatName = "mew@test.com"
                    ),
                    Chats(
                        chatName = "mrg@123.com"
                    ),
                    Chats(
                        chatName = "mew@test.com"
                    ),
                    Chats(
                        chatName = "mrg@123.com"
                    ),
                    Chats(
                        chatName = "mew@test.com"
                    ),
                    Chats(
                        chatName = "mrg@123.com"
                    ),
                    Chats(
                        chatName = "mew@test.com"
                    ),
                    Chats(
                        chatName = "mrg@123.com"
                    ),
                    Chats(
                        chatName = "mew@test.com"
                    ),
                    Chats(
                        chatName = "mrg@123.com"
                    ),
                    Chats(
                        chatName = "mew@test.com"
                    ),
                    Chats(
                        chatName = "mrg@123.com"
                    ),
                    Chats(
                        chatName = "mew@test.com"
                    ),
                    Chats(
                        chatName = "mrg@123.com"
                    ),
                    Chats(
                        chatName = "mew@test.com"
                    ),
                    Chats(
                        chatName = "mrg@123.com"
                    ),
                    Chats(
                        chatName = "mew@test.com"
                    ),
                    Chats(
                        chatName = "mrg@123.com"
                    ),
                    Chats(
                        chatName = "mew@test.com"
                    ),
                    Chats(
                        chatName = "mrg@123.com"
                    ),
                    Chats(
                        chatName = "mew@test.com"
                    ),
                )
            ),
            navigateToCurrentChat = {},
            isCardEnabled = true,
            navigateToAddChats = {},
            logOut = {}
        )
    }

}