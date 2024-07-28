package com.mad.softwares.chitchat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mad.softwares.chitchat.R
import com.mad.softwares.chitchat.data.uiState
import java.net.Authenticator

@Composable
fun Authentication(
    appUiState: uiState,
    goToChats:()->Unit,
    backHandler:()->Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        SelectionContainer{
            Text(
                text = appUiState.uniqueAuth,
                fontSize = 30.sp
            )
        }
        Text(
            text = stringResource(R.string.auth_recovery),
            modifier = Modifier.padding(20.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = goToChats
        ) {
            Text(text = stringResource(R.string.go_to_chats))
        }
    }
    BackHandler(backHandler)
}
@Preview
@Composable
fun PreviewAuthentication(){
    Authentication(
        appUiState = uiState(),
        goToChats = {},
        backHandler = {}
    )
}