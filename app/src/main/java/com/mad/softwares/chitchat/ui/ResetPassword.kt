package com.mad.softwares.chitchat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mad.softwares.chitchat.R
import com.mad.softwares.chitchat.data.uiState
import com.mad.softwares.chitchat.ui.theme.ChitChatTheme

@Composable
fun ResetPassword(
    appUiState:uiState,
    updatePassword:(String)->Unit,
    updateUsername:(String)->Unit,
    updateUniqueId:(String)->Unit,
    backHandler:()->Unit,
    goToHome:()->Unit
){
    Spacer(modifier = Modifier.height(50.dp))
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .imePadding()){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(
                text = stringResource(R.string.insert_the_details),
                modifier = Modifier.padding(20.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(30.dp))
            OutlinedTextField(
//                modifier = Modifier.padding(3.dp),
                value = appUiState.userName,
                label = { Text(text = stringResource(R.string.username))},
                onValueChange = { updateUsername(it) },
                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                isError = appUiState.wrongOtp,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "")
                },
                singleLine = true
            )
            OutlinedTextField(
//                modifier = Modifier.padding(3.dp),
                value = appUiState.password,
                label = {Text(text = stringResource(R.string.new_password))},
                onValueChange = { updatePassword(it) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                visualTransformation = if(!passwordVisible)PasswordVisualTransformation() else VisualTransformation.None,
                isError = appUiState.wrongOtp,
                trailingIcon = {
                    val icon = if(passwordVisible) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible}) {
                        androidx.compose.material.Icon(
                            imageVector = icon,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = "Show password"
                        )
                    }
                },
                singleLine = true
            )
            OutlinedTextField(
//                modifier = Modifier.padding(3.dp),
                value = appUiState.uniqueAuth,
                label = { Text(text = stringResource(R.string.authentication_id))},
                onValueChange = { updateUniqueId(it) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { goToHome() }
                ),
                isError = appUiState.wrongOtp,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription ="" )
                },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(30.dp))
            if (appUiState.isPasswordResetSuccess == isPasswordReset.No) {
                Text(
                    text = stringResource(R.string.unable_to_success_the_password),
                    color = MaterialTheme.colorScheme.error
                )
            }
            else if(appUiState.isPasswordResetSuccess == isPasswordReset.Yes){
                Text(
                    text = stringResource(R.string.password_successfully_reset_login_back),
                    color = MaterialTheme.colorScheme.onBackground
                    )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = goToHome
            ) {
                Text(text = stringResource(R.string.reset_password))
            }
        }
    }


    LoadingIndicator(isLoading = appUiState.isLoading)
    BackHandler(backHandler)
}

@Preview
@Composable
fun PreviewResetPassword(){
    ChitChatTheme(
        dynamicColor = false
    ) {
        ResetPassword(
            uiState(
                isPasswordResetSuccess = isPasswordReset.Yes
            ),{},{},{},{},{}
        )
    }
}