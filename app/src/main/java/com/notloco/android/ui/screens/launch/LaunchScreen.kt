package com.notloco.android.ui.screens.launch

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notloco.android.R
import com.notloco.android.ui.components.NLPrimaryButton
import com.notloco.android.ui.theme.CalendasFamily
import com.notloco.android.ui.theme.GeomFamily
import com.notloco.android.ui.theme.NLBackgroundColor
import com.notloco.android.ui.theme.NLPrimaryColor
import com.notloco.android.ui.theme.NLTextPrimary
import com.notloco.android.ui.theme.NLTextSecondary

@Composable
fun LaunchScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NLBackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // App illustration - iOS style centered
        Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.launch_new_ios),
            contentDescription = "NotLoco",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(20.dp))

        // App name - iOS large title style
        Text(
            text = "NotLoco",
            fontSize = 52.sp,
            lineHeight = 56.sp,
            fontFamily = CalendasFamily,
            fontWeight = FontWeight.Normal,
            color = NLTextPrimary,
            letterSpacing = 0.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tagline - iOS subtitle style
        Text(
            text = "The personal therapy and reflection space",
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontFamily = GeomFamily,
            fontWeight = FontWeight.Normal,
            color = NLTextSecondary,
            letterSpacing = (-0.4).sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Get Started button - iOS style
        NLPrimaryButton(
            text = "Get Started",
            onClick = onNavigateToSignup
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sign In link - iOS style
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?",
                color = NLTextPrimary,
                fontFamily = GeomFamily,
                fontSize = 15.sp,
                letterSpacing = (-0.3).sp
            )
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Sign In",
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = GeomFamily,
                    color = NLPrimaryColor,
                    fontSize = 15.sp,
                    letterSpacing = (-0.3).sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
