package com.mad.softwares.chitchat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mad.softwares.chitchat.R
import com.mad.softwares.chitchat.data.uiState
import com.mad.softwares.chitchat.ui.theme.ChitChatTheme

@Composable
fun OTPAuten(
    appUiState:uiState,
    goToChats:()->Unit,
    authText:(String)->Unit,
    backHandler:()->Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
        ,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = stringResource(R.string.otp),
            modifier = Modifier.padding(20.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = appUiState.uniqueAuth,
            onValueChange = { authText(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { goToChats() }
            ),
            isError = appUiState.wrongOtp,
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription ="" )
            }
        )
        Spacer(modifier = Modifier.height(30.dp))
        if(appUiState.wrongOtp){
            Text(
                text = "Wrong OTP entered please try again",
                color = MaterialTheme.colorScheme.error
                )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = goToChats
        ) {
            Text(text = stringResource(R.string.go_to_chats))
        }
    }

    LoadingIndicator(isLoading = appUiState.isLoading)
    BackHandler(backHandler)
}

@Preview
@Composable
fun PreviewOtp(){
    ChitChatTheme{ OTPAuten(
        uiState(wrongOtp = false),
        goToChats = {},
        authText = {},
        backHandler = {}
    ) }
}