package com.mad.softwares.chitchat.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import com.mad.softwares.chitchat.ui.theme.ChitChatTheme

@Composable
fun StartHere(
    appUiState:uiState,
    registerButton:()->Unit,
    loginButton:()->Unit,
    chatsButton:()->Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(80.dp)
                .clickable(onClick = registerButton, enabled = !appUiState.isLoading),
        ) {
            Column(
                modifier = modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    modifier = modifier
                        .fillMaxWidth(),
                    text = stringResource(R.string.register_with_us),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
            }
        }
        Spacer(modifier = modifier.height(20.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(80.dp)
                .clickable(onClick = loginButton,enabled = !appUiState.isLoading),
        ) {
            Column(
                modifier = modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    modifier = modifier
                        .fillMaxWidth(),
                    text = stringResource(R.string.login_now),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
            }
        }
        Spacer(modifier = modifier.height(20.dp))
//        n
    }
    LoadingIndicator(isLoading = appUiState.isLoading)
}

@Preview
@Composable
fun PreviewStartHere(){
    ChitChatTheme(
        dynamicColor = false
    ) {
        StartHere(
            registerButton= {},
            loginButton = {},
            chatsButton = {},
            appUiState = uiState()
        )
    }
}