package com.notloco.android.ui.screens.chat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.notloco.android.data.models.CoachDetails
import com.notloco.android.data.models.UiState
import com.notloco.android.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatState by viewModel.chatState.collectAsState()
    val pinnedChatState by viewModel.pinnedChatState.collectAsState()
    val coachState by viewModel.coachState.collectAsState()
    val isVideoCheckInAvailable by viewModel.isVideoCheckInAvailable.collectAsState()
    val messages = (chatState as? UiState.Success)?.data?.messages.orEmpty()
    val pinnedMessagesFromApi = (pinnedChatState as? UiState.Success)?.data?.messages.orEmpty()
    val pinnedMessages = if (pinnedMessagesFromApi.isNotEmpty()) {
        pinnedMessagesFromApi
    } else {
        messages.filter { it.isPinned }
    }
    val coach = (coachState as? UiState.Success)?.data
    var showingPinnedMessages by rememberSaveable { mutableStateOf(false) }
    val displayedMessages = if (showingPinnedMessages) pinnedMessages else messages
    val grouped = displayedMessages.groupBy { sectionLabel(it.createdAt) }
    val listState = rememberLazyListState()
    val subtitle = coach?.headline
        ?.takeIf { it.isNotBlank() }
        ?: buildCoachSubtitle(coach)

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

    LaunchedEffect(pinnedMessages.size) {
        if (pinnedMessages.isEmpty()) {
            showingPinnedMessages = false
        }
    }

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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = NLTextSecondary,
                    fontFamily = GeomFamily,
                    letterSpacing = (-0.2).sp,
                    maxLines = 2,
                    overflow = TextOverflow.Clip
                )
            }

            IconButton(
                onClick = { },
                enabled = isVideoCheckInAvailable,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isVideoCheckInAvailable) {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFE1D45C),
                                    NLPrimaryColor
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    NLTextTertiary.copy(alpha = 0.3f),
                                    NLTextTertiary.copy(alpha = 0.3f)
                                )
                            )
                        }
                    )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Videocam,
                    contentDescription = "Video check-in",
                    tint = if (isVideoCheckInAvailable) NLWhite else NLTextSecondary,
                    modifier = Modifier.size(20.dp)
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
                            TextButton(onClick = {
                                viewModel.fetchChats()
                                viewModel.fetchPinnedChats()
                            }) {
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
                        if (pinnedMessages.isNotEmpty()) {
                            item {
                                PinnedNotesChip(
                                    isSelected = showingPinnedMessages,
                                    onClick = { showingPinnedMessages = !showingPinnedMessages }
                                )
                            }
                        }

                        if (displayedMessages.isEmpty()) {
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
                    .padding(end = 16.dp, bottom = 80.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .size(108.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(108.dp)
                                .clip(CircleShape)
                                .background(NLPrimaryColor.copy(alpha = 0.08f))
                        )
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .scale(pulseScale)
                                .size(68.dp)
                                .shadow(8.dp, CircleShape, clip = false)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color(0xFFE1D45C),
                                            NLPrimaryColor,
                                            Color(0xFF67B1C0)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        ) {
                            Image(
                                painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_mic_ios),
                                contentDescription = "Record",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

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
 * iOS-style pinned notes toggle.
 */
@Composable
private fun PinnedNotesChip(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier
                .clip(RoundedCornerShape(40.dp))
                .background(if (isSelected) NLPrimaryColor else NLWhite)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.PushPin,
                    contentDescription = "Pinned",
                    tint = if (isSelected) NLWhite else NLBlack,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Pinned Notes",
                    color = if (isSelected) NLWhite else NLBlack,
                    fontFamily = GeomFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * iOS-style date section chip.
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
 * iOS-style chat card for audio messages.
 */
@Composable
private fun ChatMessageItem(message: ChatMessage) {
    val isUserMessage = message.senderType.equals("user", ignoreCase = true)
    val transcription = message.transcription ?: "Preparing transcript..."
    val bubbleShape = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = if (isUserMessage) 8.dp else 2.dp,
        bottomEnd = if (isUserMessage) 2.dp else 8.dp
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 2.dp),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        if (!isUserMessage) {
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
                .fillMaxWidth(0.82f)
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .shadow(
                            elevation = if (isUserMessage) 6.dp else 1.dp,
                            shape = bubbleShape,
                            clip = false
                        )
                        .clip(bubbleShape)
                        .then(
                            if (isUserMessage) {
                                Modifier.background(NLWhite)
                            } else {
                                Modifier.background(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color(0xFFFFB589),
                                            Color(0xFFFFF0C9)
                                        )
                                    )
                                )
                            }
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    if (message.requestVideoCheckIn) {
                        Text(
                            text = "You requested a video check-in",
                            fontSize = 16.sp,
                            color = NLTextPrimary,
                            fontFamily = GeomFamily,
                            lineHeight = 22.sp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isUserMessage) {
                                            Brush.linearGradient(
                                                listOf(Color(0xFF67B1C0), NLPrimaryColor)
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                listOf(Color(0xFFF7C47A), NLPrimaryColor)
                                            )
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = "Play",
                                    tint = NLWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            WaveformPlaceholder(
                                modifier = Modifier.weight(1f),
                                isUserMessage = isUserMessage
                            )
                            Text(
                                text = message.audioLength?.ifBlank { "--:--" } ?: "--:--",
                                fontSize = 12.sp,
                                color = NLTextPrimary,
                                fontFamily = GeomFamily
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = transcription,
                            fontSize = 15.sp,
                            color = NLTextPrimary,
                            fontFamily = GeomFamily,
                            letterSpacing = (-0.3).sp,
                            lineHeight = 21.sp
                        )
                    }

                    message.links?.takeIf { it.isNotBlank() }?.let { link ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = link,
                            fontSize = 13.sp,
                            color = NLPrimaryColor,
                            fontFamily = GeomFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (message.isPinned) {
                    Icon(
                        imageVector = Icons.Rounded.PushPin,
                        contentDescription = "Pinned",
                        tint = NLPrimaryColor,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(16.dp)
                    )
                }
            }

            message.createdAt?.takeIf { it.isNotBlank() }?.let { timestamp ->
                Text(
                    text = formatTimestamp(timestamp),
                    fontSize = 11.sp,
                    color = NLTextSecondary,
                    fontFamily = GeomFamily,
                    letterSpacing = (-0.1).sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun WaveformPlaceholder(
    modifier: Modifier = Modifier,
    isUserMessage: Boolean
) {
    val bars = listOf(4, 8, 11, 7, 5, 10, 14, 8, 6, 12, 15, 8, 5, 11, 6, 9, 13, 7)
    val accent = if (isUserMessage) Color(0xFF67B1C0) else NLPrimaryColor
    Row(
        modifier = modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        bars.forEachIndexed { index, barHeight ->
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(barHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index % 4 == 0) accent else NLTextTertiary)
            )
        }
    }
}

private fun formatTimestamp(timestamp: String): String {
    if (timestamp.isBlank()) return ""
    val date = parseServerDate(timestamp) ?: return timestamp
    val output = SimpleDateFormat("h:mm a", Locale.US)
    return output.format(date)
}

private fun buildCoachSubtitle(coach: CoachDetails?): String {
    if (coach == null) return "Tap a note to open detail"
    coach.headline?.takeIf { it.isNotBlank() }?.let { return it }
    val years = coach.yearsExperience?.let { "$it years of experience" }
    return listOfNotNull(coach.profession, years)
        .filter { it.isNotBlank() }
        .joinToString(" | ")
        .ifBlank {
            listOfNotNull(coach.profession, coach.expertise)
                .filter { it.isNotBlank() }
                .joinToString(" | ")
                .ifBlank { "Tap a note to open detail" }
        }
}

private fun parseServerDate(value: String): Date? {
    val candidates = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX"
    )

    candidates.forEach { pattern ->
        try {
            val parser = SimpleDateFormat(pattern, Locale.US)
            parser.timeZone = TimeZone.getTimeZone("UTC")
            parser.parse(value)?.let { return it }
        } catch (_: Exception) {
            // try next parser
        }
    }

    return null
}

private fun formatSectionDate(date: Date): String {
    val calendar = java.util.Calendar.getInstance().apply { time = date }
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    val suffix = when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
    val formatter = SimpleDateFormat("EEE, d'$suffix' MMM yy", Locale.US)
    return formatter.format(date)
}

private fun sectionLabel(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "Recent"
    val parsed = parseServerDate(createdAt) ?: return createdAt.substringBefore("T")
    return formatSectionDate(parsed)
}
