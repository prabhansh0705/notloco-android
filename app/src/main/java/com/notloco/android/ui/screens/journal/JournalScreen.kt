package com.notloco.android.ui.screens.journal

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.notloco.android.ui.theme.*
import com.notloco.android.utils.PermissionUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun JournalScreen() {
    val context = LocalContext.current
    var showRecordingDialog by remember { mutableStateOf(false) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = PermissionUtils.AUDIO_PERMISSIONS.toList()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Journal",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NLBackgroundColor
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (permissionsState.allPermissionsGranted) {
                        showRecordingDialog = true
                    } else {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                },
                containerColor = NLPrimaryColor,
                contentColor = NLWhite,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Entry")
            }
        },
        containerColor = NLBackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Empty state or journal list
            Text(
                text = "Your journal entries will appear here",
                fontSize = 16.sp,
                color = NLTextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    if (showRecordingDialog) {
        RecordingDialog(
            onDismiss = { showRecordingDialog = false },
            onSave = {
                // Handle save
                showRecordingDialog = false
            }
        )
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
                
                IconButton(
                    onClick = { isRecording = !isRecording },
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isRecording) "Stop" else "Start",
                        modifier = Modifier.size(48.dp),
                        tint = NLPrimaryColor
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSave,
                enabled = !isRecording && recordingTime > 0
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
