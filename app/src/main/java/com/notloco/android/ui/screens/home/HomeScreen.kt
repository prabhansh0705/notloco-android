package com.notloco.android.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.clickable
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.notloco.android.R
import com.notloco.android.ui.screens.chat.ChatScreen
import com.notloco.android.ui.screens.journal.JournalScreen
import com.notloco.android.ui.screens.profile.ProfileScreen
import com.notloco.android.ui.screens.profile.ProfileViewModel
import com.notloco.android.ui.theme.GeomFamily
import com.notloco.android.ui.theme.NLBlack
import com.notloco.android.ui.theme.NLWhite
import android.content.Intent
import android.net.Uri
import android.widget.Toast

@Composable
fun HomeScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var showProfile by remember { mutableStateOf(false) }
    val profileState by profileViewModel.profileState.collectAsState()
    val profileImageUrl = (profileState as? com.notloco.android.data.models.UiState.Success)?.data?.profilePic

    if (showProfile) {
        ProfileScreen(
            onBack = { showProfile = false },
            onMembershipClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.notloco.com"))
                context.startActivity(intent)
            },
            onSupportClick = {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:support@notloco.com")
                }
                runCatching { context.startActivity(emailIntent) }
                    .onFailure { Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show() }
            },
            onAboutUsClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.notloco.com/about"))
                runCatching { context.startActivity(intent) }
                    .onFailure { Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show() }
            },
            onLogout = onLogout
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
            0 -> ChatScreen()
            1 -> JournalScreen()
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(bottom = 6.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .align(androidx.compose.ui.Alignment.BottomCenter),
            color = NLWhite
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                SegmentedTab(
                    title = "Therapy",
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                SegmentedTab(
                    title = "Journal",
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        IconButton(
            onClick = { showProfile = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 12.dp, end = 12.dp)
                .background(NLWhite, androidx.compose.foundation.shape.CircleShape)
        ) {
            AsyncImage(
                model = profileImageUrl,
                contentDescription = "Profile",
                placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.cheetah_profile),
                error = androidx.compose.ui.res.painterResource(id = R.drawable.cheetah_profile),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(androidx.compose.foundation.shape.CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun SegmentedTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (selected) NLBlack else NLWhite)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = GeomFamily,
            color = if (selected) NLWhite else Color.Black
        )
    }
}
