package com.notloco.android.ui.screens.chat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.notloco.android.R
import com.notloco.android.data.models.ChatMessage
import com.notloco.android.data.models.UiState
import com.notloco.android.ui.theme.NLBackgroundColor
import com.notloco.android.ui.theme.NLError
import com.notloco.android.ui.theme.NLPrimaryColor
import com.notloco.android.ui.theme.NLTextPrimary
import com.notloco.android.ui.theme.NLTextSecondary
import com.notloco.android.ui.theme.NLWhite

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatState by viewModel.chatState.collectAsState()
    val coachState by viewModel.coachState.collectAsState()
    val messages = (chatState as? UiState.Success)?.data?.messages.orEmpty()
    val coach = (coachState as? UiState.Success)?.data
    val grouped = messages.groupBy { sectionLabel(it.createdAt) }
    val pulseTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NLWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NLPrimaryColor),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = coach?.profilePic,
                    contentDescription = "Coach",
                    placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.dr_lily),
                    error = androidx.compose.ui.res.painterResource(id = R.drawable.dr_lily),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column {
                Text(
                    text = coach?.name?.ifBlank { "Your Therapist" } ?: "Your Therapist",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = NLTextPrimary
                )
                Text(
                    text = listOfNotNull(coach?.profession, coach?.expertise)
                        .joinToString(" | ")
                        .ifBlank { "Tap a note to open detail" },
                    fontSize = 12.sp,
                    color = NLTextSecondary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { viewModel.fetchChats() }) {
                Text(text = "Refresh", fontSize = 13.sp, color = NLPrimaryColor)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(NLBackgroundColor)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (val state = chatState) {
                    UiState.Loading -> item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is UiState.Error -> item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = state.message, color = NLError)
                            TextButton(onClick = { viewModel.fetchChats() }) {
                                Text("Retry")
                            }
                            TextButton(onClick = { viewModel.fetchCoachDetails() }) {
                                Text("Reload Therapist")
                            }
                        }
                    }

                    is UiState.Success -> {
                        if (messages.isEmpty()) {
                            item {
                                Text(
                                    text = "No chats found yet",
                                    color = NLTextSecondary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        } else {
                            grouped.forEach { (section, sectionMessages) ->
                                item { DateChip(section) }
                                items(sectionMessages, key = { it.id }) { message ->
                                    ChatMessageItem(message)
                                }
                            }
                        }
                    }

                    else -> Unit
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .scale(pulseScale)
                            .size(72.dp)
                            .background(NLPrimaryColor, CircleShape)
                    ) {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_mic_ios),
                            contentDescription = "Record",
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Hold to record",
                        fontSize = 12.sp,
                        color = NLTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun DateChip(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = NLTextSecondary,
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(NLWhite)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    val isCoach = message.senderType.equals("coach", ignoreCase = true)
    val content = message.transcription ?: message.audioUrl ?: "Audio message"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCoach) Arrangement.Start else Arrangement.End
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.86f),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isCoach) NLWhite else Color(0xFFEFD9C5))
                    .padding(14.dp)
            ) {
                Text(text = content, fontSize = 14.sp, color = NLTextPrimary, maxLines = 4)
                message.createdAt?.takeIf { it.isNotBlank() }?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = it, fontSize = 11.sp, color = NLTextSecondary)
                }
            }
        }
    }
}

private fun sectionLabel(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "Recent"
    return if (createdAt.length >= 10) createdAt.substring(0, 10) else createdAt
}
