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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.notloco.android.R
import com.notloco.android.data.models.UiState
import com.notloco.android.data.models.UserInfo
import com.notloco.android.ui.theme.NLBackgroundColor
import com.notloco.android.ui.theme.NLError
import com.notloco.android.ui.theme.NLPrimaryColor
import com.notloco.android.ui.theme.NLTextPrimary
import com.notloco.android.ui.theme.NLTextSecondary
import com.notloco.android.ui.theme.NLWhite

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Image(
                                painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_left_arrow_ios),
                                contentDescription = "Back",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NLWhite)
            )
        },
        containerColor = NLWhite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ProfileHeader(profile)

            when (val state = profileState) {
                UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = NLError, fontSize = 12.sp)
                        TextButton(onClick = { viewModel.fetchProfile() }) {
                            Text("Retry")
                        }
                    }
                }

                else -> Unit
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DisabledInfoField(
                    icon = Icons.Default.Person,
                    text = profile?.name?.ifBlank { "-" } ?: "-"
                )
                DisabledInfoField(
                    icon = Icons.Default.Email,
                    text = profile?.email?.ifBlank { "-" } ?: "-"
                )
                DisabledInfoField(
                    icon = Icons.Default.Phone,
                    text = profile?.phoneNumber?.ifBlank { "-" } ?: "-"
                )

                Spacer(modifier = Modifier.height(8.dp))

                ProfileActionRow(
                    title = "My Membership",
                    iconRes = R.drawable.ic_subscriptions_ios,
                    onClick = onMembershipClick
                )
                ProfileActionRow(
                    title = "Customer Support",
                    iconRes = R.drawable.ic_support_ios,
                    onClick = onSupportClick
                )
                ProfileActionRow(
                    title = "About Us",
                    iconRes = R.drawable.ic_terms_ios,
                    onClick = onAboutUsClick
                )
                ProfileActionRow(
                    title = "Log Out",
                    iconRes = R.drawable.ic_logout_ios,
                    destructive = true,
                    onClick = onLogout
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(profile: UserInfo?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(NLPrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = profile?.profilePic,
                contentDescription = null,
                placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.cheetah_profile),
                error = androidx.compose.ui.res.painterResource(id = R.drawable.cheetah_profile),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.edit_profile_picture),
                contentDescription = "Edit profile",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(14.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = profile?.name?.ifBlank { "Profile" } ?: "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Light,
            color = NLTextPrimary
        )
    }
}

@Composable
private fun DisabledInfoField(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NLBackgroundColor)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = NLTextSecondary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = NLTextPrimary, fontSize = 15.sp)
    }
}

@Composable
private fun ProfileActionRow(
    title: String,
    iconRes: Int,
    destructive: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (destructive) NLError.copy(alpha = 0.1f) else NLBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = androidx.compose.ui.res.painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                color = if (destructive) NLError else NLTextPrimary,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_right_arrow_ios),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
