package com.mad.softwares.chitchat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mad.softwares.chitchat.R
import com.mad.softwares.chitchat.data.uiState

@Composable
fun FiringUp(
    appUiState: uiState
){
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(100.dp)
    ){
       Text(text = stringResource(R.string.welcome))
    }

    LoadingIndicator(isLoading = appUiState.isLoading,
        modifier = Modifier.padding(100.dp),
        contentAlignment = Alignment.BottomCenter)
}

@Preview
@Composable
fun PreviewFiringUp(){
    FiringUp(
        appUiState = uiState(
            isLoading = true
        )
    )
}