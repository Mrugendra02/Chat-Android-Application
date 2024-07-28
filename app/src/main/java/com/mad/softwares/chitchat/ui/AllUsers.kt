package com.mad.softwares.chitchat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mad.softwares.chitchat.data.User
import com.mad.softwares.chitchat.data.uiState
import com.mad.softwares.chitchat.ui.theme.ChitChatTheme


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AllUsers(
    appUiState:uiState,
    setCurrentUser:(String)->Unit,
    refreshState: PullRefreshState
)
{
    Box(modifier = Modifier
        .fillMaxSize()){
        when (appUiState.isUsersLoading) {
            isUsernamesLoading.Loading -> {
                AllUsersLoading(appUiState = appUiState, refreshState = refreshState)
            }

            isUsernamesLoading.Failed -> {
                ErroAllusers(appUiState = appUiState, refreshState = refreshState)
            }

            isUsernamesLoading.Success -> {
                AllUsersSuccess(
                    appUiState = appUiState,
                    setCurrentUser = setCurrentUser,
                    refreshState
                )
            }
        }
        LoadingIndicator(
            isLoading = appUiState.isLoading,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AllUsersSuccess(
    appUiState:uiState,
    setCurrentUser:(String)->Unit,
    refreshState: PullRefreshState
){
    var isCardEnabled by remember{
        mutableStateOf(true)
    }
   Box(modifier = Modifier
       .background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxSize()
                .pullRefresh(refreshState)
        ) {
            items(appUiState.allAvailableUsers) { user ->
                UserCard(
                    user = user,
                    setCurrentUser = {
                        setCurrentUser(it)
                        isCardEnabled = !isCardEnabled
                                     },
                    isCardEnabled = isCardEnabled
                )
            }
        }

        PullRefreshIndicator(
            refreshing = appUiState.isRefreshing,
            state = refreshState,
            modifier = Modifier
                .align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    }
    LoadingIndicator(isLoading = appUiState.isLoading)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AllUsersLoading(
    appUiState:uiState,

    refreshState: PullRefreshState
){

    Box(modifier = Modifier ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxSize()
                .pullRefresh(refreshState)
        ) {
            items(6) { user ->
                UserCardLoading(

                )
            }
        }

        PullRefreshIndicator(
            refreshing = appUiState.isRefreshing,
            state = refreshState,
            modifier = Modifier
                .align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(
    user:String,
    modifier:Modifier = Modifier,
    setCurrentUser:(String)->Unit,
    isCardEnabled:Boolean
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(5.dp)
        ,
        onClick = { setCurrentUser(user)
                  },
        enabled = isCardEnabled
    ) {
        Row(
            modifier = modifier
                .fillMaxHeight()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                modifier = modifier
                    .size(40.dp),
                imageVector = Icons.Default.Person,
                contentDescription = ""
            )
            Text(
                modifier =modifier
                    .fillMaxWidth()
                        ,
                text = user,
                fontSize = 25.sp,
                fontWeight = FontWeight(700),
                textAlign = TextAlign.Center
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCardLoading(
    modifier:Modifier = Modifier,
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(5.dp)
        ,

    ) {
        Row(
            modifier = modifier
                .fillMaxHeight()
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
//            Icon(
//                modifier = modifier
//                    .size(40.dp),
//                imageVector = Icons.Default.Person,
//                contentDescription = ""
//            )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier
                    .width(60.dp)
                    .fillMaxHeight()
            ) {}
            Spacer(Modifier.width(10.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            ) {}

        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ErroAllusers(
    appUiState:uiState,
    refreshState:PullRefreshState
){
    Box(modifier = Modifier
        .fillMaxSize(),
        )
    {
        Text(text = "Error to get all the users")

        PullRefreshIndicator(
            refreshing = appUiState.isRefreshing,
            state = refreshState,
            modifier = Modifier
                .align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun PreviewAllUsers(){
    ChitChatTheme(
        dynamicColor = false
    ) {
        AllUsers(appUiState = uiState(
            allAvailableUsers = listOf(
                "myfriend1",
                "myfriend12",
                "myfriend3",
                "myfriend4",
                "myfriend16",
            ),
            isRefreshing = true,
            isUsersLoading = isUsernamesLoading.Failed
        ),
            setCurrentUser = {},
            refreshState = rememberPullRefreshState(refreshing = true, onRefresh = { /*TODO*/ })
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewUser(){
    ChitChatTheme(
        dynamicColor = false
    ){
        UserCard(
            user = "Myfriend",
            setCurrentUser = {},
            isCardEnabled = false
        )
    }
}

@Preview
@Composable
fun PreviewUserLoading(){
    ChitChatTheme(
        dynamicColor = false
    ) {
        UserCardLoading()
    }
}