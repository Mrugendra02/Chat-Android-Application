package com.mad.softwares.chitchat.ui.chats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mad.softwares.chitchat.R

@Composable
fun ShowErrorAndRetry(
    retry:()->Unit
){
    Column(
        modifier = Modifier.padding(5.dp)
    ) {
        Text(stringResource(R.string.error_message))
        Button(onClick = { retry() }) {
            Text(text = "Retry, I guess")
        }
    }

}