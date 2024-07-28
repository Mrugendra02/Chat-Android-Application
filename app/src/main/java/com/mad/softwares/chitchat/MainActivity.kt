package com.mad.softwares.chitchat

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mad.softwares.chitchat.ui.ApplicationScreen
import com.mad.softwares.chitchat.ui.chitChatViewModel
import com.mad.softwares.chitchat.ui.theme.ChitChatTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChitChatTheme(
                dynamicColor = false
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val appViewModel:chitChatViewModel = viewModel(factory = chitChatViewModel.Factory)
                    val appUiState = appViewModel.extUiState.collectAsState().value
                   ApplicationScreen(
                       appUiState = appUiState,
                       appViewModel = appViewModel
                   )
                }
            }
        }
    }
}

