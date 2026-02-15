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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notloco.android.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.notloco.android.data.models.JournalEntry
import com.notloco.android.data.models.UiState
import com.notloco.android.ui.theme.NLBackgroundColor
import com.notloco.android.ui.theme.NLError
import com.notloco.android.ui.theme.NLPrimaryColor
import com.notloco.android.ui.theme.NLTextPrimary
import com.notloco.android.ui.theme.NLTextSecondary
import com.notloco.android.ui.theme.NLWhite
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
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "journal_mic_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NLWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Journal",
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                color = NLTextPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { viewModel.fetchJournals() }) {
                Text(text = "Refresh", fontSize = 13.sp, color = NLPrimaryColor)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(NLBackgroundColor)
        ) {
            when (val state = journalState) {
                UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = state.message, color = NLError)
                        TextButton(onClick = { viewModel.fetchJournals() }) { Text("Retry") }
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
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(journals, key = { it.id }) { entry ->
                                JournalTimelineItem(entry)
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

@Composable
private fun EmptyJournalState(
    pulseScale: Float,
    onMicClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.journal_image_ios),
            contentDescription = "Journal",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Make a Journal entry today",
            color = NLTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Light
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "What's on your mind?", color = NLTextSecondary, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .size(180.dp)
                .background(NLWhite.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .background(NLWhite.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onMicClick,
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(76.dp)
                        .background(NLPrimaryColor, CircleShape)
                ) {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_mic_ios),
                        contentDescription = "Record",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun JournalTimelineItem(entry: JournalEntry) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 6.dp)
        ) {
            Box(modifier = Modifier.size(9.dp).background(NLPrimaryColor, CircleShape))
            Box(modifier = Modifier.padding(top = 2.dp).widthLine().fillMaxHeight())
        }

        Spacer(modifier = Modifier.size(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = NLWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = sectionLabel(entry.createdAt), color = NLTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = entry.transcription?.ifBlank { "(No transcription)" } ?: "(No transcription)",
                    color = NLTextPrimary,
                    fontSize = 14.sp,
                    maxLines = 4
                )
            }
        }
    }
}

private fun Modifier.widthLine() =
    this.size(width = 1.dp, height = 54.dp).background(NLTextSecondary.copy(alpha = 0.3f))

@Composable
fun RecordingDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isRecording) "Recording..." else "Record Journal Entry",
                fontWeight = FontWeight.Bold
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
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = NLPrimaryColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                IconButton(onClick = { isRecording = !isRecording }, modifier = Modifier.size(80.dp)) {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(
                            id = if (isRecording) R.drawable.ic_pause_circle_ios else R.drawable.ic_mic_ios
                        ),
                        contentDescription = if (isRecording) "Stop" else "Start",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = !isRecording && recordingTime > 0) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun sectionLabel(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "Recent"
    return if (createdAt.length >= 10) createdAt.substring(0, 10) else createdAt
}
