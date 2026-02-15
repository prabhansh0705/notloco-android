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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.notloco.android.R
import com.notloco.android.data.models.ChatMessage
import com.notloco.android.data.models.UiState
import com.notloco.android.ui.theme.*

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatState by viewModel.chatState.collectAsState()
    val coachState by viewModel.coachState.collectAsState()
    val messages = (chatState as? UiState.Success)?.data?.messages.orEmpty()
    val coach = (coachState as? UiState.Success)?.data
    val grouped = messages.groupBy { sectionLabel(it.createdAt) }
    val listState = rememberLazyListState()

    val pulseTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NLWhite)
            .statusBarsPadding()
    ) {
        // iOS-style therapist header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Coach avatar - iOS rounded rect style
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NLBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = coach?.profilePic,
                    contentDescription = "Coach",
                    placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.dr_lily),
                    error = androidx.compose.ui.res.painterResource(id = R.drawable.dr_lily),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = coach?.name?.ifBlank { "Your Therapist" } ?: "Your Therapist",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = NLTextPrimary,
                    fontFamily = GeomFamily,
                    letterSpacing = (-0.4).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = listOfNotNull(coach?.profession, coach?.expertise)
                        .joinToString(" | ")
                        .ifBlank { "Tap a note to open detail" },
                    fontSize = 13.sp,
                    color = NLTextSecondary,
                    fontFamily = GeomFamily,
                    letterSpacing = (-0.2).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Chat area with rounded top corners - iOS style
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(NLBackgroundColor)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (val state = chatState) {
                    UiState.Loading -> item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = NLPrimaryColor,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    is UiState.Error -> item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = state.message,
                                color = NLError,
                                fontSize = 14.sp,
                                fontFamily = GeomFamily
                            )
                            TextButton(onClick = { viewModel.fetchChats() }) {
                                Text(
                                    "Retry",
                                    color = NLPrimaryColor,
                                    fontFamily = GeomFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    is UiState.Success -> {
                        if (messages.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No messages yet.\nStart a conversation!",
                                        color = NLTextSecondary,
                                        fontSize = 15.sp,
                                        fontFamily = GeomFamily,
                                        letterSpacing = (-0.3).sp,
                                        lineHeight = 22.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
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

            // iOS-style mic button at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .scale(pulseScale)
                            .size(68.dp)
                            .shadow(8.dp, CircleShape, clip = false)
                            .clip(CircleShape)
                            .background(NLPrimaryColor)
                    ) {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_mic_ios),
                            contentDescription = "Record",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hold to record",
                        fontSize = 12.sp,
                        color = NLTextSecondary,
                        fontFamily = GeomFamily,
                        letterSpacing = (-0.2).sp
                    )
                }
            }
        }
    }
}

/**
 * iOS-style date section chip
 */
@Composable
private fun DateChip(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = NLTextSecondary,
            fontFamily = GeomFamily,
            letterSpacing = (-0.1).sp,
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(NLWhite)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

/**
 * iOS-style chat message bubble with tail effect
 */
@Composable
private fun ChatMessageItem(message: ChatMessage) {
    val isCoach = message.senderType.equals("coach", ignoreCase = true)
    val content = message.transcription ?: message.audioUrl ?: "Audio message"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isCoach) Arrangement.Start else Arrangement.End
    ) {
        // Coach avatar for coach messages
        if (isCoach) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(NLBackgroundColor)
                    .align(Alignment.Bottom)
            ) {
                AsyncImage(
                    model = message.coachProfilePic,
                    contentDescription = null,
                    placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.dr_lily),
                    error = androidx.compose.ui.res.painterResource(id = R.drawable.dr_lily),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isCoach) 4.dp else 18.dp,
                        bottomEnd = if (isCoach) 18.dp else 4.dp
                    )
                )
                .background(
                    if (isCoach) NLChatBubbleCoach else NLChatBubbleUser
                )
                .then(
                    if (isCoach) Modifier.shadow(
                        1.dp,
                        RoundedCornerShape(18.dp),
                        clip = false
                    ) else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = content,
                fontSize = 15.sp,
                color = if (isCoach) NLChatBubbleCoachText else NLChatBubbleUserText,
                fontFamily = GeomFamily,
                letterSpacing = (-0.3).sp,
                lineHeight = 20.sp
            )
            message.createdAt?.takeIf { it.isNotBlank() }?.let { timestamp ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(timestamp),
                    fontSize = 11.sp,
                    color = if (isCoach) NLTextSecondary else NLWhite.copy(alpha = 0.7f),
                    fontFamily = GeomFamily,
                    letterSpacing = (-0.1).sp
                )
            }
        }
    }
}

/**
 * Format timestamp for display - iOS style "HH:mm" for today, date otherwise
 */
private fun formatTimestamp(timestamp: String): String {
    if (timestamp.isBlank()) return ""
    // Try to extract time portion "HH:mm" from ISO timestamp
    return try {
        if (timestamp.contains("T")) {
            val timePart = timestamp.substringAfter("T").substringBefore(".")
            if (timePart.length >= 5) timePart.substring(0, 5) else timePart
        } else if (timestamp.length >= 16) {
            timestamp.substring(11, 16)
        } else {
            timestamp
        }
    } catch (e: Exception) {
        timestamp
    }
}

/**
 * Group messages by date section
 */
private fun sectionLabel(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "Recent"
    return try {
        if (createdAt.contains("T")) {
            createdAt.substringBefore("T")
        } else if (createdAt.length >= 10) {
            createdAt.substring(0, 10)
        } else {
            createdAt
        }
    } catch (e: Exception) {
        "Recent"
    }
}
