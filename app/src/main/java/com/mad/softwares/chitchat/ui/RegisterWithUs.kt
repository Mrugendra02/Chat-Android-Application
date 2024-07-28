package com.mad.softwares.chitchat.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mad.softwares.chitchat.R
import com.mad.softwares.chitchat.data.uiState
import com.mad.softwares.chitchat.ui.theme.ChitChatTheme

@Composable
fun Registration(
    buttonFunction:()->Unit,
    appUiState: uiState,
    updateUsername:(String)->Unit,
    updatePassword:(String)->Unit,
    modifier:Modifier = Modifier,
    @StringRes buttonText:Int,
    forgotPassword:()->Unit,
    successButton:()->Unit,
    showForgotButton : Boolean
){
    val error:Boolean = if (showForgotButton) {
            !appUiState.userNameExist
        } else {
            appUiState.userNameExist
        }

    var passwordVisible by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ){
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = appUiState.userName,
                onValueChange = { updateUsername(it) },
                label = {
                    if (!showForgotButton) {
                        if(appUiState.emptyUsername){
                            Text(text = stringResource(R.string.username_cannot_be_empty))
                        }
                        else if (error) {
                            Text(text = stringResource(R.string.username_exist_choose_different))
                        } else {
                            Text(text = stringResource(R.string.username))
                        }
                    } else {if(appUiState.emptyUsername){
                        Text(text = stringResource(R.string.username_cannot_be_empty))
                    }
                       else if (error) {
                            Text(text = stringResource(R.string.username_does_not_exits))
                        }
                        else {
                            Text(text = stringResource(R.string.username))
                        }

                    }
                },
                isError = error || appUiState.emptyUsername,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "")
                }
            )

            OutlinedTextField(
                value = appUiState.password,
                onValueChange = { updatePassword(it) },
                singleLine = true,
                isError = !appUiState.passFound || appUiState.emptyUsername,
                label =
                {
                    if (!appUiState.passFound) {
                        Text(text = stringResource(R.string.wrong_password))
                    }
                    else if(appUiState.emptyPassword){
                        Text(text = stringResource(R.string.password_cannot_be_empty))
                    }else {
                        Text(text = stringResource(id = R.string.password))
                    }

                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = if(!passwordVisible)PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                val icon = if(passwordVisible) Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible}){
                    Icon(
                        imageVector = icon,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Show password"
                    )
                }
            }

            )
            Spacer(modifier = modifier.height(20.dp))
            Button(
                onClick = buttonFunction,
                shape = MaterialTheme.shapes.small,
                enabled = !appUiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = stringResource(buttonText),
                    color = MaterialTheme.colorScheme.onSecondary
                )

            }

            Spacer(modifier = Modifier.height(10.dp))
            if (showForgotButton) {
                Text(
                    modifier = modifier
                        .clickable(onClick = forgotPassword),
                    text = "Forgot Password?",
                    textDecoration = TextDecoration.Underline
                )
            }
//        if (appUiState.regsSuccess){
//            Button(onClick= successButton,
//                shape = MaterialTheme.shapes.small) {
//                Text(text = "Next Step",
//                    color = MaterialTheme.colorScheme.onSecondary)
//                Icon(
//                    imageVector = Icons.Rounded.Send,
//                    contentDescription =""
//                )
//            }
//        }
        }
    }
    LoadingIndicator(isLoading = appUiState.isLoading)

}

@Preview
@Composable
fun PreviewRegistration(){
    ChitChatTheme(
//        darkTheme = true
    ) {
        Registration(
            buttonFunction = { /*TODO*/ },
            appUiState = uiState(
                regsSuccess = true,
                emptyUsername = true,
                emptyPassword = true

                ),
            updateUsername = {},
            updatePassword = {},
            buttonText = R.string.register_now,
            forgotPassword = {},
            successButton = {},
            showForgotButton = true
        )
    }
}