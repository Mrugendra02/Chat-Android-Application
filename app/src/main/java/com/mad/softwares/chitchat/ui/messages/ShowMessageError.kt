package com.mad.softwares.chitchat.ui.messages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mad.softwares.chitchat.R

@Composable
fun ShowMessageError(
    retry:()->Unit
){
    Column(
        modifier = Modifier.padding(5.dp)
    ) {
        Text("Error getting the Messages due to some issues and I am not implementing this error screen so just press the below button to try again")
        Button(onClick = { retry() }) {
            Text(text = "Retry, if that's going to do any thing")
        }
    }
}

@Preview
@Composable
fun PreviewShowMessage(){
    ShowMessageError {
        
    }
}