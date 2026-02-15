package com.notloco.android.ui.screens.journal

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notloco.android.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.notloco.android.data.models.JournalEntry
import com.notloco.android.data.models.UiState
import com.notloco.android.ui.theme.*
import com.notloco.android.utils.PermissionUtils

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel()
) {
    var showRecordingDialog by remember { mutableStateOf(false) }
    val journalState by viewModel.journalState.collectAsState()
    val journals = (journalState as? UiState.Success)?.data?.transcriptions.orEmpty()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NLWhite)
            .statusBarsPadding()
    ) {
        // iOS-style header with large title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
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

        // Content area with rounded top corners - iOS style
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(NLBackgroundColor)
        ) {
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
                                top = 20.dp,
                                bottom = 100.dp  // Space for bottom tab bar
                            ),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(journals, key = { it.id }) { entry ->
                                JournalTimelineItem(entry, isLast = entry == journals.last())
                            }
                        }
                    }
                }

                else -> Unit
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
}

/**
 * iOS-style empty journal state with centered illustration and mic button
 */
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

        // iOS-style concentric circle mic button
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow ring
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(NLPrimaryColor.copy(alpha = 0.06f), CircleShape)
            )
            // Middle ring
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .background(NLPrimaryColor.copy(alpha = 0.10f), CircleShape)
            )
            // Inner mic button
            IconButton(
                onClick = onMicClick,
                modifier = Modifier
                    .scale(pulseScale)
                    .size(76.dp)
                    .shadow(8.dp, CircleShape, clip = false, ambientColor = NLPrimaryColor.copy(alpha = 0.3f))
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
 * iOS-style timeline item for journal entries
 */
@Composable
private fun JournalTimelineItem(entry: JournalEntry, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline indicator column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            // Timeline dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(NLPrimaryColor)
            )
            // Timeline line (not for last item)
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

        // Journal card - iOS style
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NLWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = formatDate(entry.createdAt),
                    color = NLTextSecondary,
                    fontSize = 12.sp,
                    fontFamily = GeomFamily,
                    letterSpacing = (-0.1).sp
                )
                Spacer(modifier = Modifier.height(6.dp))
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
                entry.audioLength?.takeIf { it.isNotBlank() }?.let { duration ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Duration: $duration",
                        color = NLTextSecondary,
                        fontSize = 12.sp,
                        fontFamily = GeomFamily,
                        letterSpacing = (-0.1).sp
                    )
                }
            }
        }
    }
}

/**
 * Format date for display
 */
private fun formatDate(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "Recent"
    return try {
        if (createdAt.contains("T")) {
            val datePart = createdAt.substringBefore("T")
            val timePart = createdAt.substringAfter("T").substringBefore(".")
            val time = if (timePart.length >= 5) timePart.substring(0, 5) else timePart
            "$datePart  $time"
        } else if (createdAt.length >= 10) {
            createdAt.substring(0, 10)
        } else {
            createdAt
        }
    } catch (e: Exception) {
        "Recent"
    }
}

/**
 * iOS-style recording dialog
 */
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
