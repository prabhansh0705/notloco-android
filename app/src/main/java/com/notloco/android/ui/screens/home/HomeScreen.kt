package com.notloco.android.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.notloco.android.ui.screens.chat.ChatScreen
import com.notloco.android.ui.screens.journal.JournalScreen
import com.notloco.android.ui.screens.profile.ProfileScreen
import com.notloco.android.ui.theme.NLBackgroundColor

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Journal : BottomNavItem("journal", Icons.Default.Book, "Journal")
    object Chat : BottomNavItem("chat", Icons.Default.Chat, "Chat")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

@Composable
fun HomeScreen() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        BottomNavItem.Journal,
        BottomNavItem.Chat,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = NLBackgroundColor
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        },
        containerColor = NLBackgroundColor
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItem) {
                0 -> JournalScreen()
                1 -> ChatScreen()
                2 -> ProfileScreen()
            }
        }
    }
}
