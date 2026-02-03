package com.notloco.android.ui.screens.launch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notloco.android.ui.components.NLPrimaryButton
import com.notloco.android.ui.theme.NLBackgroundColor
import com.notloco.android.ui.theme.NLTextPrimary
import kotlinx.coroutines.delay

@Composable
fun LaunchScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NLBackgroundColor)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // App Logo/Title
            Text(
                text = "NotLoco",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = NLTextPrimary
            )

            Text(
                text = "Your mental health companion",
                fontSize = 16.sp,
                color = NLTextPrimary
            )

            Spacer(modifier = Modifier.weight(1f))

            // Buttons
            NLPrimaryButton(
                text = "Get Started",
                onClick = onNavigateToSignup
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = NLTextPrimary
                )
                androidx.compose.material3.TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Sign In",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
