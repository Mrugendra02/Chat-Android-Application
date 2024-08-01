package com.mad.softwares.chitchat.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mad.softwares.chitchat.MyApplication
import com.mad.softwares.chitchat.ui.chats.AddChatViewModel
import com.mad.softwares.chitchat.ui.chats.ChatsViewModel
import com.mad.softwares.chitchat.ui.messages.MessagesViewModel
import com.mad.softwares.chitchat.ui.welcome.welcomeViewModel

object GodViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            welcomeViewModel(
                myApplication().container.dataRepository, myApplication().container.auth
            )
        }

        initializer {
            ChatsViewModel(
                dataRepository = myApplication().container.dataRepository,
                savedStateHandle = this.createSavedStateHandle(),
            )
        }

        initializer {
            AddChatViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                dataRepository = myApplication().container.dataRepository
            )
        }

        initializer {
            MessagesViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                dataRepository = myApplication().container.dataRepository
            )
        }
    }
}

fun CreationExtras.myApplication(): MyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)