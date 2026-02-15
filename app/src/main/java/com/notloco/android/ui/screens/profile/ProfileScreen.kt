package com.notloco.android.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.notloco.android.R
import com.notloco.android.data.models.UiState
import com.notloco.android.data.models.UserInfo
import com.notloco.android.ui.components.NLBackButton
import com.notloco.android.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: (() -> Unit)? = null,
    onMembershipClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onAboutUsClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val profileState by viewModel.profileState.collectAsState()
    val profile = (profileState as? UiState.Success<UserInfo>)?.data

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NLWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // iOS-style navigation bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (onBack != null) {
                NLBackButton(onClick = onBack)
            }
            Text(
                text = "Profile",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = GeomFamily,
                color = NLTextPrimary,
                letterSpacing = (-0.4).sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Profile header with avatar
            ProfileHeader(profile)

            Spacer(modifier = Modifier.height(8.dp))

            // Loading / Error states
            when (val state = profileState) {
                UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
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
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            state.message,
                            color = NLError,
                            fontSize = 13.sp,
                            fontFamily = GeomFamily
                        )
                        TextButton(onClick = { viewModel.fetchProfile() }) {
                            Text(
                                "Retry",
                                color = NLPrimaryColor,
                                fontFamily = GeomFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                else -> Unit
            }

            // User info fields - iOS style grouped list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Info section card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NLBackgroundColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        InfoRow(
                            icon = Icons.Default.Person,
                            text = profile?.name?.ifBlank { "-" } ?: "-"
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 44.dp),
                            thickness = 0.5.dp,
                            color = NLSeparator.copy(alpha = 0.3f)
                        )
                        InfoRow(
                            icon = Icons.Default.Email,
                            text = profile?.email?.ifBlank { "-" } ?: "-"
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 44.dp),
                            thickness = 0.5.dp,
                            color = NLSeparator.copy(alpha = 0.3f)
                        )
                        InfoRow(
                            icon = Icons.Default.Phone,
                            text = profile?.phoneNumber?.ifBlank { "-" } ?: "-"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action items - iOS style grouped list
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NLBackgroundColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        ProfileActionRow(
                            title = "My Membership",
                            iconRes = R.drawable.ic_subscriptions_ios,
                            onClick = onMembershipClick
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 44.dp),
                            thickness = 0.5.dp,
                            color = NLSeparator.copy(alpha = 0.3f)
                        )
                        ProfileActionRow(
                            title = "Customer Support",
                            iconRes = R.drawable.ic_support_ios,
                            onClick = onSupportClick
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 44.dp),
                            thickness = 0.5.dp,
                            color = NLSeparator.copy(alpha = 0.3f)
                        )
                        ProfileActionRow(
                            title = "About Us",
                            iconRes = R.drawable.ic_terms_ios,
                            onClick = onAboutUsClick
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout - separate destructive action
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onLogout),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NLError.copy(alpha = 0.06f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_logout_ios),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Log Out",
                            color = NLError,
                            fontSize = 16.sp,
                            fontFamily = GeomFamily,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_right_arrow_ios),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * iOS-style profile header with avatar and name
 */
@Composable
private fun ProfileHeader(profile: UserInfo?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with edit overlay
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = profile?.profilePic,
                contentDescription = null,
                placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.cheetah_profile),
                error = androidx.compose.ui.res.painterResource(id = R.drawable.cheetah_profile),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .shadow(4.dp, CircleShape, clip = false, ambientColor = NLBlack.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )
            // Edit badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(NLWhite)
                    .shadow(2.dp, CircleShape, clip = false),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.edit_profile_picture),
                    contentDescription = "Edit profile",
                    modifier = Modifier.size(14.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = profile?.name?.ifBlank { "Profile" } ?: "Profile",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = GeomFamily,
            color = NLTextPrimary,
            letterSpacing = (-0.3).sp
        )

        profile?.email?.takeIf { it.isNotBlank() }?.let { email ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = email,
                fontSize = 14.sp,
                fontFamily = GeomFamily,
                color = NLTextSecondary,
                letterSpacing = (-0.2).sp
            )
        }
    }
}

/**
 * iOS-style info row in grouped list
 */
@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = NLTextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = NLTextPrimary,
            fontSize = 16.sp,
            fontFamily = GeomFamily,
            letterSpacing = (-0.3).sp
        )
    }
}

/**
 * iOS-style action row in grouped settings list
 */
@Composable
private fun ProfileActionRow(
    title: String,
    iconRes: Int,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            color = NLTextPrimary,
            fontSize = 16.sp,
            fontFamily = GeomFamily,
            letterSpacing = (-0.3).sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_right_arrow_ios),
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
    }
}
