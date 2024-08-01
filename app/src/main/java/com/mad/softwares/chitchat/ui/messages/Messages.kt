package com.mad.softwares.chitchat.ui.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.mad.softwares.chitchat.R
import com.mad.softwares.chitchat.data.MessageReceived
import com.mad.softwares.chitchat.data.messageStatus
import com.mad.softwares.chitchat.ui.ApptopBar
import com.mad.softwares.chitchat.ui.GodViewModelProvider
import com.mad.softwares.chitchat.ui.destinationData
import com.mad.softwares.chitchat.ui.theme.ChitChatTheme
import java.text.SimpleDateFormat
import java.util.Date

object messagesdestinationData : destinationData {
    override val route = "Messages"
    val chatIDAndUsername = "chatID,username"
    override val title = R.string.chats
    override val canBack = true
    val routeWithArgs = "$route/{$chatIDAndUsername}"
}

@Composable
fun Messages(
    viewModel: MessagesViewModel = viewModel(factory = GodViewModelProvider.Factory),
    navigateUp: () -> Unit
) {
    val uiState = viewModel.messagesUiState.collectAsState().value
//    val currentScreen = uiState.messageScreen
    when (uiState.messageScreen) {
        MessageScreen.Loading -> {
            MessageBodyLoading()
        }

        MessageScreen.Error -> {
            ShowMessageError {
                viewModel.getMessages(isForced = true)
            }
        }

        MessageScreen.Success -> {

            MessagesBodySuccess(
                uiState = uiState,
                updateMessage = { viewModel.messageEdit(it) },
                getMessagesAgain = { viewModel.getMessages(isForced = true) },
                sendMessage = { viewModel.sendTextMessage() },
                navigateUp = navigateUp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesBodySuccess(
    uiState: MessagesUiState,
    updateMessage: (String) -> Unit,
    getMessagesAgain:()->Unit,
    sendMessage: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ApptopBar(
                destinationData = messagesdestinationData,
                navigateUp = navigateUp,
                title = uiState.chatName,
                action = {
                    IconButton(onClick = { getMessagesAgain() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "refresh"
                        )}
                }
            )
        },
        bottomBar = {
            BottomMessageSend(
                appUistate = uiState,
                sendMessage = sendMessage,
                updateMessage = updateMessage
            )
        },

        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) { padding ->
        if (uiState.messages.isNotEmpty()) {
            LazyColumn (
                modifier = modifier
                    .padding(padding)
                    .fillMaxSize(),
//                    .background(MaterialTheme.colorScheme.background),
                reverseLayout = true,
                verticalArrangement = Arrangement.Bottom
            ){
                items(uiState.messages.reversed()) {
                    if (it.senderId == uiState.chatName) {
                        ReceiverChat(message = it)
                    } else {
                        SenderChat(message = it)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
//                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier= Modifier
                        .fillMaxWidth(),
                    text = stringResource(R.string.start_chating_now),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}

@Composable
fun MessagesBodyError(
    uiState: MessagesUiState
) {
    Text(text = "Error : ${uiState.errorMessage}")
}

@Composable
fun MessageBodyLoading() {
    Text(text = "Loading")
}

@Composable
fun SenderChat(
    message: MessageReceived
) {
    val date = message.timeStamp.toDate()
//    val sdf  = SimpleDateFormat("HH:mm")
    val currentDate = Timestamp.now().toDate()
    val difference = (currentDate.time - date.time) / (1000 * 60 * 60)
    val sdf = if (difference <= 24) {
        SimpleDateFormat("hh:mm a")
    } else {
        SimpleDateFormat("YYYY/MM/dd hh:mm a")
    }
    val fDate = sdf.format(date)
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f)
                .padding(vertical = 10.dp, horizontal = 5.dp),
//                .height(60.dp),
            shape = RoundedCornerShape(20.dp, 0.dp, 20.dp, 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {

            Text(
                text = message.content,
                modifier = Modifier
                    .padding(10.dp),
                fontSize = 20.sp
            )
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier
//                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                            .padding(vertical = 2.dp),
                        text = fDate.toString(),
//                    text = difference.toString(),
//                text = message.timeStamp.toDate().toString(),
                        textAlign = TextAlign.Start
                    )
                    if (message.status == messageStatus.Send) {
                        Icon(
                            imageVector = Icons.Default.ArrowOutward,
                            contentDescription = null
                        )
                    } else if (message.status == messageStatus.Sending) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = null
                        )
                    } else if (message.status == messageStatus.Error) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null
                        )
                    }
                }
            }
        }


    }
}

@Composable
fun ReceiverChat(
    message: MessageReceived
) {
    val date = message.timeStamp.toDate()
//    val sdf  = SimpleDateFormat("HH:mm")
    val currentDate = Timestamp.now().toDate()
    val difference = (currentDate.time - date.time) / (1000 * 60 * 60)
    val sdf = if (difference <= 24) {
        SimpleDateFormat("hh:mm a")
    } else {
        SimpleDateFormat("YYYY/MM/dd hh:mm a")
    }
    val fDate = sdf.format(date)
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f)
                .padding(vertical = 10.dp, horizontal = 5.dp),
//                .height(60.dp),
            shape = RoundedCornerShape(0.dp, 20.dp, 20.dp, 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = message.content,
                modifier = Modifier
                    .padding(10.dp),
                fontSize = 20.sp
            )
            Card {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .padding(vertical = 2.dp),
                    text = fDate.toString(),
//                    text = difference.toString(),
//                text = message.timeStamp.toDate().toString(),
                    textAlign = TextAlign.End
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

    }
}

@Composable
fun BottomMessageSend(
    appUistate: MessagesUiState,
    sendMessage: () -> Unit,
    updateMessage: (String) -> Unit,
) {

    ElevatedCard(
        Modifier
//            .animateContentSize(
//
//                animationSpec = spring(
//                    dampingRatio = Spring.DampingRatioNoBouncy,
//                    stiffness = Spring.StiffnessLow,
//                )

//            )
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,

            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
//                .padding(vertical = 16.dp, horizontal = 16.dp)
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
//                .height(height)
                verticalAlignment = Alignment.Bottom,

                ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
//                    .padding(end=40.dp)
                        .fillMaxWidth()
                        .padding(4.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessLow,
                            )
                        ),

//                    .padding(end = 80.dp),
                    value = appUistate.messageToSend,
//                    value = appUistate.messageToSend,
//                    onValueChange = { updateMessage(it) },
//                    value = "",
                    onValueChange = { updateMessage(it) },
                    textStyle = TextStyle.Default.copy(
                        fontSize = 20.sp,

                        ),
                    maxLines = 3,
                    placeholder = { Text(text = "Message...") },
//                    placeholder = {"message..."},
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0, 0, 0, alpha = 0),
                        unfocusedBorderColor = Color(0, 0, 0, alpha = 0),
                        disabledBorderColor = Color(0, 0, 0, alpha = 0)
                    )
//                trailingIcon =
//                {
//
//                }

                )
//            if(appUistate.messageToSend!="")
                AnimatedVisibility(
                    visible = appUistate.messageToSend.isNotEmpty(),
//                    visible = true,
                    enter = slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(
                            durationMillis = 100,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(
                            durationMillis = 100,
                            easing = LinearOutSlowInEasing
                        )
                    )
                )
                {
                    IconButton(
                        onClick = sendMessage,
                        modifier = Modifier
//                        .fillMaxWidth()
                            .padding(bottom = 10.dp, end = 10.dp)
                            .size(45.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(10.dp)
                                .size(80.dp),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "send"
                        )
                    }
                }

            }
        }
    }

}


@Preview
@Composable
fun PreviewMessagebodySuccess() {
    ChitChatTheme(
        dynamicColor = false,
        darkTheme = true
    ) {
        MessagesBodySuccess(
            uiState = MessagesUiState(
                chatName = "ThereSelf",
                messages = mutableListOf(
//                    MessageReceived(
//                        content = "Hello Friend",
//                        senderId = "mySelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 1, 25, 20, 15))
//                    ),
//                    MessageReceived(
//                        content = "Hey there",
//                        senderId = "ThereSelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 1, 25, 20, 17))
//                    ),
//                    MessageReceived(
//                        content = "good to say",
//                        senderId = "mySelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 2, 25, 20, 15))
//                    ),
//                    MessageReceived(
//                        content = "Ok Bye",
//                        senderId = "ThereSelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 2, 25, 20, 15))
//                    ),
//                    MessageReceived(
//                        content = "Hello Friend",
//                        senderId = "mySelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 1, 25, 20, 15))
//                    ),
//                    MessageReceived(
//                        content = "Hey there",
//                        senderId = "ThereSelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 1, 25, 20, 17))
//                    ),
//                    MessageReceived(
//                        content = "good to say",
//                        senderId = "mySelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 2, 25, 20, 15))
//                    ),
//                    MessageReceived(
//                        content = "Ok Bye",
//                        senderId = "ThereSelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 2, 25, 20, 15))
//                    ),
//                    MessageReceived(
//                        content = "Hello Friend",
//                        senderId = "mySelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 1, 25, 20, 15))
//                    ),
//                    MessageReceived(
//                        content = "Hey there",
//                        senderId = "ThereSelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 1, 25, 20, 17))
//                    ),
//                    MessageReceived(
//                        content = "good to say",
//                        senderId = "mySelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 2, 25, 20, 15))
//                    ),
//                    MessageReceived(
//                        content = "Ok Bye",
//                        senderId = "ThereSelf",
//                        timeStamp = Timestamp(Date(2024 - 1900, 2, 25, 20, 15))
//                    ),
                ),
            ),
            updateMessage = {},
            getMessagesAgain = {  },
            sendMessage = {},
            navigateUp = {}
        )
    }
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewSenderChat() {
    ChitChatTheme(
        dynamicColor = false,
        darkTheme = true
    ) {
        SenderChat(
            message = MessageReceived(
                content = "Hello Friend "
            )
        )
    }
}

@Preview
@Composable
fun PreviewReceiverChat() {
    ChitChatTheme(
        dynamicColor = false
    ) {
        ReceiverChat(
            message = MessageReceived(
                content = "Hello Friend \nThis is your friend",
                timeStamp = Timestamp(Date(2024 - 1900, 1, 25, 20, 15))
            )
        )
    }
}