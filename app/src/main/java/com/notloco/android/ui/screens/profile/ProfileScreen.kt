package com.notloco.android.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notloco.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NLBackgroundColor
                )
            )
        },
        containerColor = NLBackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(NLPrimaryColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(60.dp),
                    tint = NLWhite
                )
            }

            Text(
                text = "User Name",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NLTextPrimary
            )

            Text(
                text = "user@example.com",
                fontSize = 14.sp,
                color = NLTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Options
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NLWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Edit,
                        title = "Edit Profile",
                        onClick = { }
                    )
                    Divider()
                    ProfileMenuItem(
                        icon = Icons.Default.CardMembership,
                        title = "Membership",
                        onClick = { }
                    )
                    Divider()
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        onClick = { }
                    )
                    Divider()
                    ProfileMenuItem(
                        icon = Icons.Default.Logout,
                        title = "Logout",
                        onClick = { },
                        isDestructive = true
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = if (isDestructive) NLError else NLTextPrimary
        )
        Text(
            text = title,
            fontSize = 16.sp,
            color = if (isDestructive) NLError else NLTextPrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = NLGray
        )
    }
}
