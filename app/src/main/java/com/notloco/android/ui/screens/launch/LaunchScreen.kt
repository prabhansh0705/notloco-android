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
            .padding(horizontal = 28.dp, vertical = 36.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.launch_new_ios),
            contentDescription = "NotLoco",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "NotLoco",
            fontSize = 54.sp,
            lineHeight = 58.sp,
            fontFamily = CalendasFamily,
            color = NLTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "The personal therapy and reflection space",
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontFamily = GeomFamily,
            color = NLTextSecondary
        )

        Spacer(modifier = Modifier.weight(1f))

        NLPrimaryButton(
            text = "Get Started",
            onClick = onNavigateToSignup
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                color = NLTextPrimary,
                fontFamily = GeomFamily,
                fontSize = 14.sp
            )
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Sign In",
                    fontWeight = FontWeight.Medium,
                    fontFamily = GeomFamily,
                    color = NLPrimaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}
