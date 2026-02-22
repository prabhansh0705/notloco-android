package com.notloco.android.ui.screens.journal

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notloco.android.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.runtime.LaunchedEffect
import com.notloco.android.data.models.JournalEntry
import com.notloco.android.data.models.UiState
import androidx.compose.ui.platform.LocalContext
import com.notloco.android.ui.theme.*
import com.notloco.android.utils.AudioPlayerManager
import com.notloco.android.utils.PermissionUtils
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showRecordingDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<JournalEntry?>(null) }
    val journalState by viewModel.journalState.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val journals = (journalState as? UiState.Success)?.data.orEmpty()
    val playingUrl by AudioPlayerManager.playingUrl.collectAsState()

    val permissionsState = rememberMultiplePermissionsState(
        permissions = PermissionUtils.AUDIO_PERMISSIONS.toList()
    )

    val pulseTransition = rememberInfiniteTransition(label = "journal_mic_pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "journal_mic_scale"
    )

    var journalAccessEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NLBackgroundColor)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: "Journal" centered
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Journal",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = GeomFamily,
                    color = NLTextPrimary,
                    letterSpacing = 0.37.sp
                )
            }

            // "Journal access to therapist" toggle row (green toggle like iOS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Journal access to therapist",
                    fontSize = 14.sp,
                    fontFamily = GeomFamily,
                    color = NLPrimaryColor,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = journalAccessEnabled,
                    onCheckedChange = { journalAccessEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NLWhite,
                        checkedTrackColor = NLSuccess,
                        uncheckedThumbColor = NLWhite,
                        uncheckedTrackColor = NLTextTertiary
                    ),
                    modifier = Modifier.height(24.dp)
                )
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        tint = NLPrimaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Content area fills remaining space in the Column
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val state = journalState) {
                    UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = NLPrimaryColor,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    is UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.message,
                                color = NLError,
                                fontSize = 14.sp,
                                fontFamily = GeomFamily
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.fetchJournals() }) {
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
                        if (journals.isEmpty()) {
                            EmptyJournalState(
                                pulseScale = pulseScale,
                                onMicClick = {
                                    if (permissionsState.allPermissionsGranted) {
                                        showRecordingDialog = true
                                    } else {
                                        permissionsState.launchMultiplePermissionRequest()
                                    }
                                }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 12.dp,
                                    bottom = 140.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                itemsIndexed(journals, key = { _, entry -> entry.id }) { index, entry ->
                                    if (index == journals.lastIndex && viewModel.canLoadMore) {
                                        LaunchedEffect(journals.size) {
                                            viewModel.loadNextPage()
                                        }
                                    }
                                    JournalTimelineItem(
                                        entry = entry,
                                        isLast = index == journals.lastIndex && !viewModel.canLoadMore,
                                        isPlaying = playingUrl == entry.audioUrl,
                                        onClick = { selectedEntry = entry },
                                        onPlayClick = {
                                            entry.audioUrl?.let { url ->
                                                AudioPlayerManager.playOrToggle(context, url)
                                            }
                                        }
                                    )
                                }
                                if (isLoadingMore) {
                                    item(key = "loading_more") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                color = NLPrimaryColor,
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }

        // Bottom bar: pill-shaped text box + mic button (matching iOS)
        if (journals.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 20.dp, end = 20.dp, bottom = 80.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .shadow(4.dp, RoundedCornerShape(24.dp), clip = false)
                        .clip(RoundedCornerShape(24.dp))
                        .background(NLWhite)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "What's on your mind?",
                        fontSize = 15.sp,
                        color = NLPrimaryColor,
                        fontFamily = GeomFamily,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-0.2).sp
                    )
                }
                IconButton(
                    onClick = {
                        if (permissionsState.allPermissionsGranted) {
                            showRecordingDialog = true
                        } else {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    },
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(52.dp)
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
                        contentDescription = "Record journal",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    if (showRecordingDialog) {
        RecordingDialog(
            onDismiss = { showRecordingDialog = false },
            onSave = {
                showRecordingDialog = false
                viewModel.fetchJournals()
            }
        )
    }

    selectedEntry?.let { entry ->
        JournalDetailSheet(
            entry = entry,
            onDismiss = { selectedEntry = null }
        )
    }
}

@Composable
private fun EmptyJournalState(
    pulseScale: Float,
    onMicClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.journal_image_ios),
            contentDescription = "Journal",
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(160.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Make a Journal entry today",
            color = NLTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Light,
            fontFamily = GeomFamily,
            letterSpacing = (-0.3).sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "What's on your mind?",
            color = NLTextSecondary,
            fontSize = 16.sp,
            fontFamily = GeomFamily,
            letterSpacing = (-0.3).sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(NLPrimaryColor.copy(alpha = 0.06f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .background(NLPrimaryColor.copy(alpha = 0.10f), CircleShape)
            )
            IconButton(
                onClick = onMicClick,
                modifier = Modifier
                    .scale(pulseScale)
                    .size(76.dp)
                    .shadow(8.dp, CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(NLPrimaryColor)
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_mic_ios),
                    contentDescription = "Record",
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

/**
 * Timeline item matching iOS journal card layout:
 * - Date/time at top
 * - Mood + action icons (eye, trash) on SAME row
 * - Waveform with play button + duration
 * - Transcription text
 */
@Composable
private fun JournalTimelineItem(
    entry: JournalEntry,
    isLast: Boolean = false,
    isPlaying: Boolean = false,
    onClick: () -> Unit = {},
    onPlayClick: () -> Unit = {}
) {
    val mood = entry.chatgptResponse?.takeIf { it.isNotBlank() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline dot + line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(24.dp)
                .fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(NLPrimaryColor)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .weight(1f)
                        .background(NLTextSecondary.copy(alpha = 0.2f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            // Date OUTSIDE the card
            Text(
                text = formatDate(entry.createdAt),
                color = NLTextSecondary,
                fontSize = 12.sp,
                fontFamily = GeomFamily,
                letterSpacing = (-0.1).sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Journal card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable { onClick() }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Mood + action icons on SAME row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mood:  ",
                                color = Color(0xFFEA6A72),
                                fontSize = 14.sp,
                                fontFamily = GeomFamily,
                                fontWeight = FontWeight.ExtraBold
                            )
                            if (mood != null) {
                                Text(
                                    text = mood,
                                    color = NLTextPrimary,
                                    fontSize = 14.sp,
                                    fontFamily = GeomFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Text(
                                    text = "Analyzing Audio...",
                                    color = NLPrimaryColor,
                                    fontSize = 14.sp,
                                    fontFamily = GeomFamily,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = "View",
                                tint = NLPrimaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(2.dp))
                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFEA6A72),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                // Waveform + play button + duration
                entry.audioLength?.takeIf { it.isNotBlank() }?.let { duration ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        JournalPlayButton(
                            isPlaying = isPlaying,
                            onClick = onPlayClick
                        )
                        JournalWaveform(modifier = Modifier.weight(1f))
                        Text(
                            text = duration,
                            fontSize = 12.sp,
                            color = NLTextPrimary,
                            fontFamily = GeomFamily
                        )
                    }
                }

                // Transcription
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = entry.transcription?.ifBlank { "(No transcription)" }
                        ?: "(No transcription)",
                    color = NLTextPrimary,
                    fontSize = 15.sp,
                    fontFamily = GeomFamily,
                    letterSpacing = (-0.3).sp,
                    lineHeight = 20.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                }
            }
        }
    }
}

@Composable
private fun JournalWaveform(modifier: Modifier = Modifier) {
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

@Composable
private fun JournalPlayButton(
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val primary = Color(0xFF67B1C0)
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
                        listOf(primary, cream)
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

/**
 * Format ISO date to iOS-style "DD Mon YY, HH:mm"
 */
private fun formatDate(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "Recent"
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX"
    )
    val outputFormat = SimpleDateFormat("dd MMM yy, HH:mm", Locale.US)
    for (pattern in patterns) {
        try {
            val parser = SimpleDateFormat(pattern, Locale.US)
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(createdAt) ?: continue
            return outputFormat.format(date)
        } catch (_: Exception) { }
    }
    return try {
        if (createdAt.contains("T")) {
            val datePart = createdAt.substringBefore("T")
            val timePart = createdAt.substringAfter("T").substringBefore(".")
            val time = if (timePart.length >= 5) timePart.substring(0, 5) else timePart
            "$datePart, $time"
        } else {
            createdAt
        }
    } catch (_: Exception) {
        "Recent"
    }
}

@Composable
fun RecordingDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = NLWhite,
        title = {
            Text(
                text = if (isRecording) "Recording..." else "Record Journal Entry",
                fontWeight = FontWeight.SemiBold,
                fontFamily = GeomFamily,
                fontSize = 17.sp,
                letterSpacing = (-0.4).sp,
                color = NLTextPrimary
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isRecording) {
                    Text(
                        text = "${recordingTime / 60}:${String.format("%02d", recordingTime % 60)}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = GeomFamily,
                        color = NLPrimaryColor,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                IconButton(
                    onClick = { isRecording = !isRecording },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (isRecording) NLError.copy(alpha = 0.1f) else NLPrimaryColor.copy(alpha = 0.1f))
                ) {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(
                            id = if (isRecording) R.drawable.ic_pause_circle_ios else R.drawable.ic_mic_ios
                        ),
                        contentDescription = if (isRecording) "Stop" else "Start",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSave,
                enabled = !isRecording && recordingTime > 0
            ) {
                Text(
                    "Save",
                    color = NLPrimaryColor,
                    fontFamily = GeomFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = NLTextSecondary,
                    fontFamily = GeomFamily,
                    fontSize = 15.sp
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalDetailSheet(
    entry: JournalEntry,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val mood = entry.chatgptResponse?.takeIf { it.isNotBlank() }
    val currentPlayingUrl by AudioPlayerManager.playingUrl.collectAsState()
    val isPlaying = currentPlayingUrl == entry.audioUrl
    var isEditing by remember { mutableStateOf(false) }
    var editedTranscription by remember(entry.id) {
        mutableStateOf(entry.transcription ?: "")
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
                    text = formatDate(entry.createdAt),
                    color = NLTextSecondary,
                    fontSize = 15.sp,
                    fontFamily = GeomFamily,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Card with mood + audio player
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Mood row + delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mood: ",
                                color = Color(0xFFEA6A72),
                                fontSize = 15.sp,
                                fontFamily = GeomFamily,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = mood ?: "Analyzing Audio...",
                                color = if (mood != null) NLTextPrimary else NLPrimaryColor,
                                fontSize = 15.sp,
                                fontFamily = GeomFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontStyle = if (mood == null) FontStyle.Italic else FontStyle.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFEA6A72),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Audio player row
                        entry.audioLength?.takeIf { it.isNotBlank() }?.let { duration ->
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
                                JournalPlayButton(
                                    isPlaying = isPlaying,
                                    onClick = {
                                        entry.audioUrl?.let { url ->
                                            AudioPlayerManager.playOrToggle(context, url)
                                        }
                                    }
                                )
                                JournalWaveform(modifier = Modifier.weight(1f))
                                Text(
                                    text = duration,
                                    fontSize = 12.sp,
                                    color = NLTextPrimary,
                                    fontFamily = GeomFamily
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Full transcription text
                if (isEditing) {
                    androidx.compose.material3.OutlinedTextField(
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
                        text = entry.transcription?.ifBlank { "(No transcription)" }
                            ?: "(No transcription)",
                        color = NLTextPrimary,
                        fontSize = 15.sp,
                        fontFamily = GeomFamily,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            // Edit/Save FAB
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
