package com.notloco.android.ui.screens.chat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
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
import com.notloco.android.utils.AudioPlayerManager
import com.notloco.android.utils.AudioRecorder
import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val appContext = LocalContext.current
    val chatState by viewModel.chatState.collectAsState()
    val pinnedChatState by viewModel.pinnedChatState.collectAsState()
    val playingUrl by AudioPlayerManager.playingUrl.collectAsState()
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
    val sendState by viewModel.sendState.collectAsState()
    var showingPinnedMessages by rememberSaveable { mutableStateOf(false) }
    var selectedMessageForActions by rememberSaveable { mutableStateOf<ChatMessage?>(null) }
    var selectedMessageForDetail by remember { mutableStateOf<ChatMessage?>(null) }
    var showRecordingScreen by remember { mutableStateOf(false) }
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
                .padding(start = 20.dp, end = 64.dp, top = 14.dp, bottom = 14.dp),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(NLBackgroundColor)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 20.dp,
                    bottom = 160.dp
                ),
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
                                    ChatMessageItem(
                                        message = message,
                                        isPlaying = playingUrl == (message.audioUrl ?: message.audio),
                                        onPlayClick = {
                                            val url = message.audioUrl ?: message.audio
                                            url?.let { AudioPlayerManager.playOrToggle(appContext, it) }
                                        },
                                        onMessageClick = { selectedMessageForDetail = message },
                                        onMessageLongClick = { selectedMessageForActions = message }
                                    )
                                }
                            }
                        }
                    }

                    else -> Unit
                }
            }

            // Floating mic button
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clickable { showRecordingScreen = true },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .background(NLPrimaryColor.copy(alpha = 0.08f))
                    )
                    Box(
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
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_mic_ios),
                            contentDescription = "Record",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            selectedMessageForActions?.let { message ->
                val context = LocalContext.current
                MessageActionsBottomSheet(
                    message = message,
                    onDismiss = { selectedMessageForActions = null },
                    onCopyTranscript = {
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)
                            ?.setPrimaryClip(ClipData.newPlainText("Transcript", message.transcription ?: ""))
                        selectedMessageForActions = null
                    },
                    onViewFullTranscript = {
                        selectedMessageForActions = null
                        selectedMessageForDetail = message
                    }
                )
            }

            selectedMessageForDetail?.let { message ->
                ChatDetailSheet(
                    message = message,
                    onDismiss = { selectedMessageForDetail = null }
                )
            }
        }
    }

    if (showRecordingScreen) {
        ChatRecordingScreen(
            coach = coach,
            sendState = sendState,
            onSend = { file, isVanish ->
                viewModel.sendMessage(file, isVanish)
            },
            onSent = {
                viewModel.resetSendState()
                showRecordingScreen = false
            },
            onBack = {
                showRecordingScreen = false
            }
        )
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
 * Receiver shows profile icon; user messages do not. Bubble colors: user = white, receiver = yellowish.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    isPlaying: Boolean = false,
    onPlayClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onMessageLongClick: () -> Unit = {}
) {
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
                                            NLCoachBubbleGradientStart,
                                            NLCoachBubbleGradientEnd
                                        )
                                    )
                                )
                            }
                        )
                        .combinedClickable(
                            onClick = onMessageClick,
                            onLongClick = onMessageLongClick,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ChatPlayButton(
                                isPlaying = isPlaying,
                                isUserMessage = isUserMessage,
                                onClick = onPlayClick
                            )
                            AudioWaveformBars(
                                modifier = Modifier.weight(1f)
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
                            lineHeight = 21.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
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
    val accent = if (isUserMessage) Color(0xFF67B1C0) else NLPrimaryColor
    val barCount = 24
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { index ->
            val barHeight = listOf(4, 8, 11, 7, 5, 10, 14, 8, 6, 12, 15, 8, 5, 11, 6, 9, 13, 7)[index % 18]
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(barHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index % 4 == 0) accent else NLTextTertiary)
            )
        }
    }
}

@Composable
private fun ChatPlayButton(
    isPlaying: Boolean,
    isUserMessage: Boolean,
    onClick: () -> Unit
) {
    val primary = if (isUserMessage) Color(0xFF67B1C0) else NLPrimaryColor
    val cream = NLBackgroundColor
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(NLWhite)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = primary,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
            )
        }
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(primary, cream),
                        start = androidx.compose.ui.geometry.Offset(0f, Float.MAX_VALUE),
                        end = androidx.compose.ui.geometry.Offset(Float.MAX_VALUE, 0f)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AudioWaveformBars(modifier: Modifier = Modifier) {
    val barCount = 40
    val heights = listOf(3, 6, 10, 5, 8, 12, 4, 9, 14, 7, 3, 11, 6, 8, 13, 5, 10, 4, 7, 12,
        6, 9, 3, 11, 8, 5, 14, 7, 10, 4, 12, 6, 8, 3, 9, 11, 5, 13, 7, 10)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(heights[index % heights.size].dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(NLTextPrimary.copy(alpha = 0.7f))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageActionsBottomSheet(
    message: ChatMessage,
    onDismiss: () -> Unit,
    onCopyTranscript: () -> Unit,
    onViewFullTranscript: () -> Unit
) {
    val fullTranscript = message.transcription ?: ""
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Message options",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = NLTextPrimary,
                fontFamily = GeomFamily,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedButton(
                onClick = onViewFullTranscript,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View full transcript", fontFamily = GeomFamily)
            }
            OutlinedButton(
                onClick = onCopyTranscript,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy transcript", fontFamily = GeomFamily)
            }
            if (fullTranscript.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = fullTranscript,
                    fontSize = 14.sp,
                    color = NLTextSecondary,
                    fontFamily = GeomFamily,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
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

private fun formatDetailDate(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "Recent"
    val date = parseServerDate(createdAt) ?: return "Recent"
    val output = SimpleDateFormat("dd MMM yy, HH:mm", Locale.US)
    return output.format(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatDetailSheet(
    message: ChatMessage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isUserMessage = message.senderType.equals("user", ignoreCase = true)
    val currentPlayingUrl by AudioPlayerManager.playingUrl.collectAsState()
    val audioUrl = message.audioUrl ?: message.audio
    val isPlayingThis = currentPlayingUrl == audioUrl
    var isEditing by remember { mutableStateOf(false) }
    var editedTranscription by remember(message.id) {
        mutableStateOf(message.transcription ?: "")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NLWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NLTextTertiary)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Date
                Text(
                    text = formatDetailDate(message.createdAt),
                    color = NLTextSecondary,
                    fontSize = 15.sp,
                    fontFamily = GeomFamily,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Audio player card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUserMessage) NLWhite else Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .then(
                                if (!isUserMessage) {
                                    Modifier.background(
                                        brush = Brush.radialGradient(
                                            listOf(
                                                NLCoachBubbleGradientStart,
                                                NLCoachBubbleGradientEnd
                                            )
                                        )
                                    )
                                } else Modifier
                            )
                            .padding(16.dp)
                    ) {
                        // Audio player row
                        message.audioLength?.takeIf { it.isNotBlank() }?.let { duration ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFFFFF6E8),
                                                Color(0xFFFFF0D6)
                                            )
                                        )
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                if (isUserMessage) {
                                                    listOf(Color(0xFF67B1C0), NLPrimaryColor)
                                                } else {
                                                    listOf(Color(0xFFF7C47A), NLPrimaryColor)
                                                }
                                            )
                                        )
                                        .clickable {
                                            audioUrl?.let { AudioPlayerManager.playOrToggle(context, it) }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingThis) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                        contentDescription = if (isPlayingThis) "Pause" else "Play",
                                        tint = NLWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                WaveformPlaceholder(
                                    modifier = Modifier.weight(1f),
                                    isUserMessage = isUserMessage
                                )
                                Text(
                                    text = duration,
                                    fontSize = 12.sp,
                                    color = NLTextSecondary,
                                    fontFamily = GeomFamily
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Full transcription text
                if (isEditing) {
                    OutlinedTextField(
                        value = editedTranscription,
                        onValueChange = { editedTranscription = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = GeomFamily,
                            color = NLTextPrimary,
                            lineHeight = 24.sp
                        ),
                        minLines = 4
                    )
                } else {
                    Text(
                        text = message.transcription?.ifBlank { "Preparing transcript..." }
                            ?: "Preparing transcript...",
                        color = NLTextPrimary,
                        fontSize = 15.sp,
                        fontFamily = GeomFamily,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            // Edit/Save FAB - only for user messages (matching iOS logic)
            if (isUserMessage) {
                FloatingActionButton(
                    onClick = { isEditing = !isEditing },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 36.dp),
                    containerColor = NLPrimaryColor,
                    shape = CircleShape
                ) {
                    if (isEditing) {
                        Text(
                            text = "Save",
                            color = NLWhite,
                            fontFamily = GeomFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = NLWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Edit",
                                color = NLWhite,
                                fontFamily = GeomFamily,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatRecordingScreen(
    coach: CoachDetails?,
    sendState: UiState<ChatMessage>,
    onSend: (File, Boolean) -> Unit,
    onSent: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val recorder = remember { AudioRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingDone by remember { mutableStateOf(false) }
    var recordedFile by remember { mutableStateOf<File?>(null) }
    var elapsedSeconds by remember { mutableStateOf(0) }
    var isVanish by remember { mutableStateOf(false) }
    var additionalUrl by remember { mutableStateOf("") }
    val maxSeconds = 180
    val progress = elapsedSeconds.toFloat() / maxSeconds.toFloat()

    val subtitle = coach?.headline
        ?.takeIf { it.isNotBlank() }
        ?: buildCoachSubtitle(coach)

    LaunchedEffect(sendState) {
        if (sendState is UiState.Success) onSent()
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                kotlinx.coroutines.delay(1000)
                elapsedSeconds++
                if (elapsedSeconds >= maxSeconds) {
                    recordedFile = recorder.stopRecording()
                    isRecording = false
                    recordingDone = true
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (recorder.isRecording()) recorder.cancelRecording()
            AudioPlayerManager.stop()
        }
    }

    val waveTransition = rememberInfiniteTransition(label = "rec_wave")
    val wavePhase by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rec_wave_phase"
    )

    val tealArc = Color(0xFF67B1C0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NLWhite)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Back button
            IconButton(
                onClick = {
                    if (recorder.isRecording()) recorder.cancelRecording()
                    AudioPlayerManager.stop()
                    onBack()
                },
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = NLBlack)
            }

            // Coach header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(NLBackgroundColor)
                ) {
                    AsyncImage(
                        model = coach?.profilePic,
                        contentDescription = "Coach",
                        placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.dr_lily),
                        error = androidx.compose.ui.res.painterResource(id = R.drawable.dr_lily),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Column {
                    Text(
                        text = coach?.name?.ifBlank { "Your Therapist" } ?: "Your Therapist",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        fontFamily = GeomFamily,
                        color = NLTextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        fontFamily = GeomFamily,
                        color = NLTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Gradient divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFE1D45C), NLPrimaryColor, Color(0xFF67B1C0))
                        )
                    )
            )

            // Main content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (!recordingDone) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        // Waveform + mic button combined (iOS layout)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Waveform bars behind the mic button
                            if (isRecording) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                        .height(60.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(40) { i ->
                                        val h = (8 + 30 * kotlin.math.sin((i + wavePhase * 40) * 0.4)).dp
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(h)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(Color(0xFFE5C76B))
                                        )
                                    }
                                }
                            }

                            // Concentric rings
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF5F0E0).copy(alpha = 0.4f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF5F0E0).copy(alpha = 0.7f))
                            )

                            // Blue progress arc (time remaining indicator)
                            if (isRecording) {
                                Canvas(modifier = Modifier.size(110.dp)) {
                                    drawArc(
                                        color = tealArc,
                                        startAngle = -90f,
                                        sweepAngle = 360f * progress,
                                        useCenter = false,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = 4.dp.toPx(),
                                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                                        )
                                    )
                                }
                            }

                            // Inner white button with border
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .shadow(6.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(NLWhite)
                                    .clickable {
                                        val hasPerm = ContextCompat.checkSelfPermission(
                                            context, Manifest.permission.RECORD_AUDIO
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (!hasPerm) return@clickable
                                        if (!isRecording) {
                                            elapsedSeconds = 0
                                            recorder.startRecording()
                                            isRecording = true
                                        } else {
                                            recordedFile = recorder.stopRecording()
                                            isRecording = false
                                            recordingDone = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isRecording) {
                                    Text(
                                        text = String.format(
                                            "%02d:%02d",
                                            elapsedSeconds / 60,
                                            elapsedSeconds % 60
                                        ),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = GeomFamily,
                                        color = NLPrimaryColor
                                    )
                                } else {
                                    // Gradient mic icon matching iOS
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        Color(0xFFE1D45C),
                                                        NLPrimaryColor,
                                                        Color(0xFF67B1C0)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_mic_ios),
                                            contentDescription = "Record",
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = if (isRecording) "Recording has started.\nTap the button to stop recording."
                            else "Tap the button to start recording.",
                            color = Color(0xFFD9BA6B),
                            fontSize = 15.sp,
                            fontFamily = GeomFamily,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    val playingUrl by AudioPlayerManager.playingUrl.collectAsState()
                    val isPlayingPreview = recordedFile != null &&
                            playingUrl == recordedFile?.absolutePath

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        // Audio preview card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NLBackgroundColor)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF67B1C0), NLPrimaryColor)
                                        )
                                    )
                                    .clickable {
                                        recordedFile?.let {
                                            AudioPlayerManager.playOrToggle(context, it.absolutePath)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlayingPreview) Icons.Filled.Pause
                                    else Icons.Filled.PlayArrow,
                                    contentDescription = if (isPlayingPreview) "Pause" else "Play",
                                    tint = NLWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            WaveformPlaceholder(
                                modifier = Modifier.weight(1f),
                                isUserMessage = true
                            )
                            IconButton(
                                onClick = {
                                    AudioPlayerManager.stop()
                                    recordedFile?.delete()
                                    recordedFile = null
                                    recordingDone = false
                                    elapsedSeconds = 0
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Delete, "Delete",
                                    tint = NLError,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Add URL field
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NLBackgroundColor)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("\uD83D\uDD17", fontSize = 14.sp)
                            BasicTextField(
                                value = additionalUrl,
                                onValueChange = { additionalUrl = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = GeomFamily,
                                    color = NLPrimaryColor
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerField ->
                                    if (additionalUrl.isEmpty()) {
                                        Text(
                                            "Add any URL",
                                            color = NLPrimaryColor.copy(alpha = 0.5f),
                                            fontSize = 14.sp,
                                            fontFamily = GeomFamily
                                        )
                                    }
                                    innerField()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Send button
                        val isSending = sendState is UiState.Loading
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = !isSending) {
                                    recordedFile?.let { file -> onSend(file, isVanish) }
                                }
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isSending) {
                                CircularProgressIndicator(
                                    color = NLBlack,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    "Send",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = GeomFamily,
                                    color = NLBlack
                                )
                                Icon(
                                    Icons.Filled.Send, "Send",
                                    tint = NLBlack,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        if (sendState is UiState.Error) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = (sendState as UiState.Error).message,
                                color = NLError,
                                fontSize = 13.sp,
                                fontFamily = GeomFamily
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Vanish toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Checkbox(
                    checked = isVanish,
                    onCheckedChange = { isVanish = it }
                )
                Text("Vanish after 24 hours", fontSize = 16.sp, fontFamily = GeomFamily, color = NLBlack)
                Spacer(modifier = Modifier.width(4.dp))
                Text("\u23F1", fontSize = 16.sp)
            }
        }
    }
}
