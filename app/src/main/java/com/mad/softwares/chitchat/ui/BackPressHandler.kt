package com.mad.softwares.chitchat.ui

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner

@Composable
fun BackHandler(
    onBackPressed: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val backDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val backDispatcher = backDispatcherOwner?.onBackPressedDispatcher ?: return
    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }

    DisposableEffect(lifecycleOwner, backDispatcherOwner) {
        backDispatcher.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}