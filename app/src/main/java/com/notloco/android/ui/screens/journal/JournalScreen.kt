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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import android.view.ViewGroup
import com.notloco.android.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.notloco.android.data.models.JournalEntry
import com.notloco.android.data.models.UiState
import androidx.compose.ui.platform.LocalContext
import com.notloco.android.ui.theme.*
import com.notloco.android.utils.AudioPlayerManager
import com.notloco.android.utils.PermissionUtils
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** Curve height in dp for the hero image bottom arc. */
private const val HERO_CURVE_HEIGHT_DP = 50f

/**
 * Shape that clips its content with a concave-downward arch at the bottom.
 * Top and sides are straight; the bottom edge arches upward at the centre
 * from (W, H) through ≈ (W/2, H−curveHeight) to (0, H).
 * Edges extend to full height; the centre is trimmed, producing an arch.
 *
 * Using clip instead of a Canvas overlay ensures the curve works correctly
 * on top of AndroidView (native ExoPlayer PlayerView), which otherwise
 * renders above Compose content regardless of z-order.
 */
private class CurvedBottomClipShape(private val curveHeightDp: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val curveHeight = with(density) { curveHeightDp.dp.toPx() }
        val path = Path().apply {
            // Top-left → Top-right
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            // Right edge all the way down to full height
            lineTo(size.width, size.height)
            // Curved bottom: right → centre (arches up to H−C) → left
            quadraticBezierTo(
                size.width / 2f, size.height - 2f * curveHeight,
                0f, size.height
            )
            close()
        }
        return Outline.Generic(path)
    }
}

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
    val playbackProgress by AudioPlayerManager.playbackProgress.collectAsState()
    val videoUrl by viewModel.videoUrl.collectAsState()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NLBackgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                                videoUrl = videoUrl,
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
                            JournalEntriesList(
                                journals = journals,
                                isLoadingMore = isLoadingMore,
                                canLoadMore = viewModel.canLoadMore,
                                playingUrl = playingUrl,
                                playbackProgress = playbackProgress,
                                videoUrl = videoUrl,
                                onEntryClick = { selectedEntry = it },
                                onPlayClick = { entry ->
                                    entry.audioUrl?.let { url ->
                                        AudioPlayerManager.playOrToggle(context, url)
                                    }
                                },
                                onLoadMore = { viewModel.loadNextPage() },
                                pulseScale = pulseScale,
                                onMicClick = {
                                    if (permissionsState.allPermissionsGranted) {
                                        showRecordingDialog = true
                                    } else {
                                        permissionsState.launchMultiplePermissionRequest()
                                    }
                                }
                            )
                        }
                    }

                    else -> Unit
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
    videoUrl: String?,
    pulseScale: Float,
    onMicClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
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

        // Hero media: video (if available) or static image with curved bottom baked in
        JournalHeroMedia(videoUrl = videoUrl)

        Spacer(modifier = Modifier.height(32.dp))

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
            color = NLPrimaryColor,
            fontSize = 16.sp,
            fontFamily = GeomFamily,
            letterSpacing = (-0.3).sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Concentric circle mic button
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
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFFE1D45C),
                                NLPrimaryColor,
                                Color(0xFF67B1C0)
                            )
                        )
                    )
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_mic_ios),
                    contentDescription = "Record",
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

/**
 * Journal entries list with hero image header, timeline items, and bottom input bar.
 */
@Composable
private fun JournalEntriesList(
    journals: List<JournalEntry>,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    playingUrl: String?,
    playbackProgress: Float,
    videoUrl: String?,
    onEntryClick: (JournalEntry) -> Unit,
    onPlayClick: (JournalEntry) -> Unit,
    onLoadMore: () -> Unit,
    pulseScale: Float,
    onMicClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 140.dp)
        ) {
            // Hero header item: title + video/image with curved bottom
            item(key = "hero_header") {
                Column {
                    // Title
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
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

                    // Hero media: video or static image with curved bottom overlay
                    JournalHeroMedia(videoUrl = videoUrl)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Therapist access toggle (matching iOS)
                    TherapistAccessToggle()

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Journal timeline entries
            itemsIndexed(journals, key = { _, entry -> entry.id }) { index, entry ->
                if (index == journals.lastIndex && canLoadMore) {
                    LaunchedEffect(journals.size) {
                        onLoadMore()
                    }
                }
                JournalTimelineItem(
                    entry = entry,
                    isLast = index == journals.lastIndex && !canLoadMore,
                    isPlaying = playingUrl == entry.audioUrl,
                    progress = if (playingUrl == entry.audioUrl) playbackProgress else 0f,
                    onClick = { onEntryClick(entry) },
                    onPlayClick = { onPlayClick(entry) }
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

        // Bottom bar: pill-shaped text box + mic button
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
                onClick = onMicClick,
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

/**
 * Therapist access toggle row matching iOS design.
 * Shows "Journal access to therapist" with a toggle switch and info icon.
 */
@Composable
private fun TherapistAccessToggle() {
    var isEnabled by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Journal access to therapist",
            color = NLPrimaryColor,
            fontSize = 14.sp,
            fontFamily = GeomFamily,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.2).sp,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isEnabled,
            onCheckedChange = { isEnabled = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF4CAF50),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            ),
            modifier = Modifier.height(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Info",
                tint = NLTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Timeline item matching Figma journal card layout:
 * - Date/time at top
 * - Mood + share icon on SAME row
 * - Waveform with play button + duration (warm background)
 * - Transcription text
 */
@Composable
private fun JournalTimelineItem(
    entry: JournalEntry,
    isLast: Boolean = false,
    isPlaying: Boolean = false,
    progress: Float = 0f,
    onClick: () -> Unit = {},
    onPlayClick: () -> Unit = {}
) {
    val mood = entry.chatgptResponse?.takeIf { it.isNotBlank() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline dot + dashed line
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
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                ) {
                    drawLine(
                        color = Color(0xFF8E8E93).copy(alpha = 0.3f),
                        start = androidx.compose.ui.geometry.Offset(size.width / 2, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width / 2, size.height),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(6.dp.toPx(), 4.dp.toPx()),
                            0f
                        )
                    )
                }
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
                    // Mood + eye/delete icons on SAME row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mood: ",
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

                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
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
                    }

                    // Waveform + play button + duration (warm background)
                    entry.audioLength?.takeIf { it.isNotBlank() }?.let { duration ->
                        Spacer(modifier = Modifier.height(8.dp))
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
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            JournalPlayButton(
                                isPlaying = isPlaying,
                                onClick = onPlayClick
                            )
                            JournalWaveform(
                                modifier = Modifier.weight(1f),
                                progress = progress
                            )
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
private fun JournalWaveform(
    modifier: Modifier = Modifier,
    progress: Float = 0f
) {
    val barCount = 40
    val heights = listOf(3, 6, 10, 5, 8, 12, 4, 9, 14, 7, 3, 11, 6, 8, 13, 5, 10, 4, 7, 12,
        6, 9, 3, 11, 8, 5, 14, 7, 10, 4, 12, 6, 8, 3, 9, 11, 5, 13, 7, 10)
    val playedIndex = (progress * barCount).toInt()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { index ->
            val barColor = if (index <= playedIndex && progress > 0f) {
                NLTextPrimary.copy(alpha = 0.9f)
            } else {
                NLTextPrimary.copy(alpha = 0.35f)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(heights[index % heights.size].dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
private fun JournalPlayButton(
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val teal = Color(0xFF67B1C0)
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(NLWhite)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Outer ring in teal
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = teal,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
        // Inner gradient circle (teal to cream)
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(teal, Color(0xFFFFF9EA))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = teal,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Hero media composable: shows looping muted video if available,
 * otherwise falls back to the static journal image.
 *
 * The curved-bottom effect is achieved by drawing a background-coloured overlay
 * on top of the bottom corners. A quadratic Bézier arc sweeps from (0, H−C)
 * through the centre at (W/2, ≈ H) to (W, H−C); everything between that arc
 * and the box bottom is filled with [NLBackgroundColor], masking the image/video
 * corners and producing the convex-downward curve matching Figma / iOS.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun JournalHeroMedia(videoUrl: String?) {
    val context = LocalContext.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val clipShape = remember { CurvedBottomClipShape(HERO_CURVE_HEIGHT_DP) }

    if (videoUrl != null) {
        // Video player matching iOS: muted, auto-play, looping
        val videoHeightDp = (screenWidthDp * 0.48f).dp

        val exoPlayer = remember(videoUrl) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(videoUrl))
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0f
                prepare()
                playWhenReady = true
            }
        }

        DisposableEffect(videoUrl) {
            onDispose {
                exoPlayer.release()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(videoHeightDp)
                .clip(clipShape)
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        // Static image clipped to curved bottom shape
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(clipShape)
        ) {
            Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.journal_image_ios),
                contentDescription = "Journal",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * Format ISO date to "DD Mon YY, H.MMpm" style matching Figma
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
    val outputFormat = SimpleDateFormat("dd MMM yy, h.mma", Locale.US)
    for (pattern in patterns) {
        try {
            val parser = SimpleDateFormat(pattern, Locale.US)
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(createdAt) ?: continue
            return outputFormat.format(date).replace("AM", "am").replace("PM", "pm")
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
    val detailProgress by AudioPlayerManager.playbackProgress.collectAsState()
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
                        // Mood row + eye/delete
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
                                JournalWaveform(
                                    modifier = Modifier.weight(1f),
                                    progress = if (isPlaying) detailProgress else 0f
                                )
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
